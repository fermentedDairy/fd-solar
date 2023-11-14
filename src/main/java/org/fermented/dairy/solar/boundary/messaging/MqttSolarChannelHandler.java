package org.fermented.dairy.solar.boundary.messaging;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.mqtt.ReceivingMqttMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.fermented.dairy.solar.controller.repository.TimeSeriesRepository;
import org.fermented.dairy.solar.entity.messaging.DataPoint;

/**
 * Reactive Message Consumer. Consumes message from the Solar channel.
 */
@ApplicationScoped
public class MqttSolarChannelHandler {

    private final TimeSeriesRepository timeSeriesRepository;

    private Set<String> RECORDING_TOPICS = Set.of(
            "solar_assistant/inverter_1/grid_voltage/state",
            "solar_assistant/inverter_1/grid_frequency/state",
            "solar_assistant/battery_1/power/state",
            "solar_assistant/inverter_1/temperature/state",
            "solar_assistant/inverter_1/max_charge_current/state",
            "solar_assistant/battery_1/state_of_charge/state",
            "solar_assistant/battery_1/current/state"
    );

    @Inject
    public MqttSolarChannelHandler(final TimeSeriesRepository timeSeriesRepository) {
        this.timeSeriesRepository = timeSeriesRepository;
    }

    private static final Logger log = Logger.getLogger(MqttSolarChannelHandler.class.getName());

    /**
     * Consumer method.
     *
     * @param message messages to consume
     * @return
     */
    @Incoming("inverterstate")
    @Incoming("batterystate")
    @Incoming("totalstate")
    public Uni<List<DataPoint>> consume(final Message<byte[]> message) {
        try {
            final String topic = ((ReceivingMqttMessage) message).getTopic();
            if (RECORDING_TOPICS.contains(topic)) {
                timeSeriesRepository.recordData(new DataPoint(topic, new String(message.getPayload())));
            }

        } catch (final Exception th) {
            //Not too worried here, I want to consume the msg anyway, attempt to process the ones behind it.
            log.log(Level.SEVERE, th, () -> "Could not process message.");
        }
        return null;
    }
}
