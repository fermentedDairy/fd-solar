package org.fermented.dairy.solar.controller.repository;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;
import io.agroal.api.AgroalDataSource;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import org.fermented.dairy.solar.entity.exception.RepositoryException;
import org.fermented.dairy.solar.entity.messaging.DataPoint;

/**
 * Repository for time series data.
 */
@ApplicationScoped
public class TimeSeriesRepository extends AbstractRepository {

    private static final Logger log = Logger.getLogger(TimeSeriesRepository.class.getName());

    private static final String INSERT_SQL = """
            INSERT INTO solar.solardata
                (time, topic, value)
                VALUES(?, ?, ?);
            """;

    private static final String SELECT_LAST_DATAPOINT = """
            SELECT time, topic, value from solar.solardata order by time desc limit 1
            """;

    private final AgroalDataSource defaultDataSource;

    @Inject
    public TimeSeriesRepository(final AgroalDataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    /**
     * Record data point into time series store.
     *
     * @param dataPoint Data point to record
     */
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
                    "Could not record data points",
                    sqlEx);
        }

    }

    public void recordDatapoints(final List<DataPoint> list) {
        try (final Connection conn = defaultDataSource.getConnection();
             final PreparedStatement ps = getPreparedStatement(
                     conn,
                     INSERT_SQL)
        ) {
            for (int i = 0; i < list.toArray().length; i++) {
                final DataPoint dataPoint = list.get(i);
                ps.setObject(1, dataPoint.offsetDateTime());
                ps.setString(2, dataPoint.topic());
                ps.setString(3, dataPoint.value());
                ps.addBatch();
                if ((i + 1) % 30 == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
        } catch (final SQLException sqlEx) {
            throw new RepositoryException(
                    "Could not record data points",
                    sqlEx);
        }
    }
}
