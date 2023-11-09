package org.fermented.dairy.solar.controller.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import org.fermented.dairy.solar.entity.messaging.MqttMessage;

/**
 * Consumes messages for storage in time series storage,
 */
@ApplicationScoped
public class TimeSeriesService {

    public void store(final MqttMessage message){
        //TODO: save time series data
    }
}
