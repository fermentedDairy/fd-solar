package org.fermented.dairy.solar.controller.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.fermented.dairy.solar.controller.repository.ConfigRepository;
import org.fermented.dairy.solar.entity.dao.TemperatureRegions;

/**
 * Consumer to control charge current maximum levels.
 */
@ApplicationScoped
public class ChargeCurrentService {
    private static final String INVERTER_TEMPERATURE = "solar_assistant/inverter_1/temperature/state";
    private static final String MAX_CHARGE_CURRENT = "solar_assistant/inverter_1/max_charge_current/state";
    private final ConfigRepository configRepo;

    private static final AtomicInteger desiredCurrent = new AtomicInteger(-1);

    @Inject
    public ChargeCurrentService(final ConfigRepository configRepo) {
        this.configRepo = configRepo;
    }

    /**
     * Gets the required current.
     *
     * @param dataName The name of the data being processed
     * @param value The value of the data being processed
     *
     * @return The current
     */
    public Optional<Integer> getRequiredCurrent(final String dataName, final String value) {

        final TemperatureRegions regions = configRepo.getCurrentRegionsForInverterTemperature();
        if (INVERTER_TEMPERATURE.equals(dataName)) {
            final int requiredCurrent = regions.getSettingValue(Double.parseDouble(value));
            if (requiredCurrent != desiredCurrent.get()) {
                desiredCurrent.getAndSet(requiredCurrent);
                return Optional.of(requiredCurrent);
            }
        } else if (MAX_CHARGE_CURRENT.equals(dataName) && desiredCurrent.get() != Integer.parseInt(value)) {
            return Optional.of(desiredCurrent.get());
        }
        return Optional.empty();
    }
}
