package org.fermented.dairy.solar.boundary.messaging;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.mqtt.ReceivingMqttMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
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
    private final Emitter<DataPoint> emitterForDataPoints;

    @Inject
    public MqttSolarChannelHandler(final TimeSeriesRepository timeSeriesRepository,
                                   @Channel("data") final Emitter<DataPoint> emitterForDataPoints) {
        this.timeSeriesRepository = timeSeriesRepository;
        this.emitterForDataPoints = emitterForDataPoints;
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
    public CompletionStage<Void> consume(final Message<byte[]> message) {
        try {
            final String topic = ((ReceivingMqttMessage) message).getTopic();
            emitterForDataPoints.send(
                    new DataPoint(topic, new String(message.getPayload()))
            );
        } catch (final Exception th) {
            //Not too worried here, I want to consume the msg anyway, attempt to process the ones behind it.
            log.log(Level.SEVERE, th, () -> "Could not process message.");
        }
        return message.ack();
    }

    @Incoming("data")
    public void consume(final Multi<DataPoint> multiData) {
        multiData.collect().asList().invoke(timeSeriesRepository::recordDatapoints);
    }
}
