package org.fermented.dairy.solar.controller.repository;

import io.agroal.api.AgroalDataSource;
import jakarta.ejb.Asynchronous;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.fermented.dairy.solar.entity.exception.RepositoryException;
import org.fermented.dairy.solar.entity.messaging.DataPoint;

/**
 * Repository for time series data.
 */
@ApplicationScoped
public class TimeSeriesRepository extends AbstractRepository {


    private static final String INSERT_SQL = """
            INSERT INTO solar.solardata
                (time, topic, value)
                VALUES(?, ?, ?);
            """;

    private static final String SELECT_LAST_DATAPOINT = """
            SELECT time, topic, value from solar.solardata order by time desc limit 1
            """;

    private final AgroalDataSource defaultDataSource;

    @SuppressWarnings("CdiInjectionPointsInspection")//False positive for some reason :(
    @Inject
    public TimeSeriesRepository(final AgroalDataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    /**
     * Record data point into time series store.
     *
     * @param dataPoint Data point to record
     */
    @Asynchronous
    public void recordData(final DataPoint dataPoint) {
        try (final Connection conn = defaultDataSource.getConnection();
             final PreparedStatement ps = getPreparedStatement(
                     conn,
                     INSERT_SQL,
                     dataPoint.offsetDateTime(),
                     dataPoint.topic(),
                     dataPoint.value())
        ) {
            ps.executeUpdate();
        } catch (final SQLException sqlEx) {
            throw new RepositoryException(
                    "Could not record data points",
                    sqlEx);
        }
    }

    /**
     * Retrieve the most recent data point recorded.
     *
     * @return The most recent data point, empty optional if none can be found.
     */
    public Optional<DataPoint> getLastDatapoint() {
        try (
                final Connection conn = defaultDataSource.getConnection();
                final PreparedStatement pstmnt = getPreparedStatement(conn, SELECT_LAST_DATAPOINT);
                final ResultSet rs = pstmnt.executeQuery()
                ) {

            if (rs.next()) {
                return Optional.of(
                        new DataPoint(
                                rs.getObject("time", OffsetDateTime.class),
                                rs.getString("topic"),
                                rs.getString("value")
                        )
                );
            }
            return Optional.empty();
        } catch (final SQLException sqlEx) {
            throw new RepositoryException(
                    "Could not load data point",
                    sqlEx);
        }

    }
}
