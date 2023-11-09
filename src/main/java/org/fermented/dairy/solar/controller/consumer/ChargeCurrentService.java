package org.fermented.dairy.solar.controller.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.fermented.dairy.solar.controller.repository.ConfigRepository;
import org.fermented.dairy.solar.entity.dao.TemperatureRegions;
import org.fermented.dairy.solar.entity.messaging.MqttMessage;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Consumer to control charge current maximum levels.
 */
@ApplicationScoped
public class ChargeCurrentService {

    private static final Logger log = Logger.getLogger(ChargeCurrentService.class.getName());
    private static final String INVERTER_TEMPERATURE = "solar_assistant/inverter_1/temperature/state";
    private static final String MAX_CHARGE_CURRENT = "solar_assistant/inverter_1/max_charge_current/state";
    private final ConfigRepository configRepo;

    private static final AtomicInteger desiredCurrent = new AtomicInteger(-1);

    @Inject
    public ChargeCurrentService(final ConfigRepository configRepo) {
        this.configRepo = configRepo;
    }

    public Optional<Integer> getRequiredCurrent(final MqttMessage message){
        log.info(() -> "Processing message: %s".formatted(message));

        final TemperatureRegions regions = configRepo.getCurrentRegionsForInverterTemperature();
        if (INVERTER_TEMPERATURE.equals(message.topic())) {
            final int requiredCurrent = regions.getSettingValue(Double.parseDouble(message.value()));
            if (requiredCurrent != desiredCurrent.get()) {
                desiredCurrent.getAndSet(requiredCurrent);
                return Optional.of(requiredCurrent);
            }
        } else if (MAX_CHARGE_CURRENT.equals(message.topic()) && desiredCurrent.get() != Integer.parseInt(message.value())) {
            return Optional.of(desiredCurrent.get());
        }
        return Optional.empty();
    }
}
