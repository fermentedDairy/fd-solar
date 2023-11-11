package org.fermented.dairy.solar.controller.repository;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.fermented.dairy.solar.entity.exception.RepositoryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Repository for time series data.
 */
@ApplicationScoped
public class TimeSeriesRepository extends AbstractRepository {

    private static final String INSERT_SQL = """
            INSERT INTO solar.solardata
                                  (topic, value)
                                  VALUES(?, ?);
            """;

    private final AgroalDataSource defaultDataSource;

    @Inject
    public TimeSeriesRepository(final AgroalDataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    /**
     * Record datapoint inf time series store.
     *
     * @param name Name of the data
     * @param value Value to be recorded
     */
    public void recordData(final String name, final String value) {
        try (final Connection conn = defaultDataSource.getConnection();
             final PreparedStatement ps = getPreparedStatement(conn, INSERT_SQL, name, value);
        ) {
            ps.executeUpdate();
        } catch (final SQLException sqlEx) {
            throw new RepositoryException(
                    () -> "Could not record data point named '%s' with value '%s'".formatted(name, value),
                    sqlEx);
        }
    }


}
