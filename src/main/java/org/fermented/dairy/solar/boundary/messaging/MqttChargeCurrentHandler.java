package org.fermented.dairy.solar.boundary.messaging;

import io.smallrye.reactive.messaging.mqtt.ReceivingMqttMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.logging.Logger;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.fermented.dairy.solar.controller.consumer.ChargeCurrentService;

/**
 * Reactive Messaging producer for MQTT channels.
 */
@ApplicationScoped
public class MqttChargeCurrentHandler {

    private static final Logger log = Logger.getLogger(MqttChargeCurrentHandler.class.getName());

    private final ChargeCurrentService chargeCurrentService;

    @Inject
    public MqttChargeCurrentHandler(final ChargeCurrentService chargeCurrentService) {
        this.chargeCurrentService = chargeCurrentService;
    }

    /**
     * Consume inverter temperature and max solar charge current.
     *
     * @param message incoming message
     * @return PublisherBuilder of message containing new max charge current
     */
    @Incoming("maxSolarChargeCurrentState")
    @Incoming("inverterTemperatureState")
    @Outgoing("maxSolarChargeCurrentSet")
    public PublisherBuilder<Message<Integer>> setSolarChargeCurrent(final Message<byte[]> message) {

        final Optional<Integer> chargeCurrent = chargeCurrentService.getRequiredCurrent(
                ((ReceivingMqttMessage) message).getTopic(), new String(message.getPayload()));

        if (chargeCurrent.isPresent()) {
            log.info(() -> "Setting solar charge current to %d".formatted(chargeCurrent.get()));
            return ReactiveStreams.of(Message.of(chargeCurrent.get()));
        }
        return ReactiveStreams.empty();
    }
}
