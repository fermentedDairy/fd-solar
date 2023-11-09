package org.fermented.dairy.solar.boundary.messaging;

import io.smallrye.reactive.messaging.mqtt.ReceivingMqttMessage;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.fermented.dairy.solar.controller.consumer.TimeSeriesService;
import org.fermented.dairy.solar.entity.messaging.MqttMessage;

/**
 * Reactive Message Consumer. Consumes message from the Solar channel.
 */
@ApplicationScoped
public class MqttSolarChannelHandler {

    private final TimeSeriesService timeSeriesService;

    @Inject
    public MqttSolarChannelHandler(final TimeSeriesService timeSeriesService) {
        this.timeSeriesService = timeSeriesService;
    }

    private static final Logger log = Logger.getLogger(MqttSolarChannelHandler.class.getName());

    /**
     * Consumer method.
     *
     * @param message the message
     * @return ack CompletionStage
     */
    @Incoming("solarState")
    public CompletionStage<Void> consume(final Message<byte[]> message) {
        // process your price.
        try {
            timeSeriesService.store(
                    new MqttMessage(((ReceivingMqttMessage) message).getTopic(), new String(message.getPayload()))
            );

        } catch (final Exception th) {
            log.log(Level.SEVERE, th, () -> "Could not process message."); //Not too worried here, I want to consume the msg anyway, attempt to process the ones behind it.
        }
        return message.ack();
    }
}
