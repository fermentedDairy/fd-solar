package org.fermented.dairy.solar.controller.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.fermented.dairy.solar.entity.dao.TemperatureRegions;

@ApplicationScoped
public class ConfigRepository {

    public TemperatureRegions getCurrentRegionsForInverterTemperature() {
        //TODO: make this configurable in the DB
        return new TemperatureRegions(
                40,
                50,
                70,
                50,
                30
        );
    }
}
