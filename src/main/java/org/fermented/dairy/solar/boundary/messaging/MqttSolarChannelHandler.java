package org.fermented.dairy.solar.boundary.messaging;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.mqtt.ReceivingMqttMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
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

    @Inject
    public MqttSolarChannelHandler(final TimeSeriesRepository timeSeriesRepository) {
        this.timeSeriesRepository = timeSeriesRepository;
    }

    private static final Logger log = Logger.getLogger(MqttSolarChannelHandler.class.getName());

    /**
     * Consumer method.
     *
     * @param multiMessage the Multi of messages to process
     * @return
     */
    @Incoming("inverterstate")
    @Incoming("batterystate")
    @Incoming("totalstate")
    public Uni<List<DataPoint>> consume(final Multi<Message<byte[]>> multiMessage) {
        // process your price.
        try {

            return multiMessage.map(message ->
                    new DataPoint(((ReceivingMqttMessage) message).getTopic(), new String(message.getPayload())))
                    .collect().asList().invoke(timeSeriesRepository::recordData);

        } catch (final Exception th) {
            //Not too worried here, I want to consume the msg anyway, attempt to process the ones behind it.
            log.log(Level.SEVERE, th, () -> "Could not process message.");
        }
        return null;
    }
}
