package org.fermented.dairy.solar.controller.repository;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
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


}
