package org.fermented.dairy.solar.controller.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Parent abstract class for repositories.
 */
public abstract class AbstractRepository {

    /**
     * Convenience method to allow for clean try-with-resource blocks where prepared statements require parameters.
     *
     * @param conn The connection
     * @param insertSql the SQL to be executed
     * @param params The parameters
     * @return PreparedStatement (NB! Calling code must close PreparedStatement)
     * @throws SQLException SQLException thrown if PreparedStatement and parameter setting fails
     */
    protected PreparedStatement getPreparedStatement(
            final Connection conn,
            final String insertSql,
            final String... params) throws SQLException {
        final PreparedStatement ps = conn.prepareStatement(insertSql); //NOSONAR: java:S2095, must be closed by calling class
        for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);

        return ps;
    }
}
