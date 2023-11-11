package org.fermented.dairy.solar.controller.repository;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.fermented.dairy.solar.entity.dao.TemperatureRegions;
import org.fermented.dairy.solar.entity.exception.RepositoryException;

/**
 * Repository for config data.
 */
@ApplicationScoped
public class ConfigRepository extends AbstractRepository {

    private static final String SELECT_BY_NAME = """
            SELECT value
            FROM solar.config where name = ?
            """;

    private static final TemperatureRegions DEFAULT_TEMP_REGION = new TemperatureRegions(
            40,
            50,
            70,
            50,
            30
    );

    private final AgroalDataSource defaultDataSource;

    @Inject
    public ConfigRepository(final AgroalDataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    /**
     * Get temperature regions.
     *
     * @return configured temperature regions or default.
     */
    public TemperatureRegions getCurrentRegionsForInverterTemperature() {

        return new TemperatureRegions(
                getConfigByName("TemperatureRegions.t1").map(Double::parseDouble).orElse(DEFAULT_TEMP_REGION.t1()),
                getConfigByName("TemperatureRegions.t2").map(Double::parseDouble).orElse(DEFAULT_TEMP_REGION.t2()),
                getConfigByName("TemperatureRegions.green").map(Integer::parseInt).orElse(DEFAULT_TEMP_REGION.green()),
                getConfigByName("TemperatureRegions.yellow").map(Integer::parseInt).orElse(DEFAULT_TEMP_REGION.yellow()),
                getConfigByName("TemperatureRegions.red").map(Integer::parseInt).orElse(DEFAULT_TEMP_REGION.red())
        );
    }

    /**
     * Get configuration by name.
     *
     * @param name The name of the config.
     *
     * @return Optional of the value.
     */
    public Optional<String> getConfigByName(final String name) {
        try (final Connection conn = defaultDataSource.getConnection();
             final PreparedStatement ps = getPreparedStatement(conn, SELECT_BY_NAME, name);
             final ResultSet rs = ps.executeQuery()
        ) {
            if (rs.next()) {
                return Optional.ofNullable(rs.getString("value"));
            }
            return Optional.empty();
        } catch (final SQLException e) {
            throw new RepositoryException(() -> "Could not load config with name %s".formatted(name), e);
        }
    }
}
