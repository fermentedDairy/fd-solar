package org.fermented.dairy.solar.controller.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.fermented.dairy.solar.controller.repository.TimeSeriesRepository;
import org.fermented.dairy.solar.entity.messaging.MqttMessage;

/**
 * Consumes messages for storage in time series storage.
 */
@ApplicationScoped
public class TimeSeriesService {

    private final TimeSeriesRepository timeSeriesRepository;

    @Inject
    public TimeSeriesService(final TimeSeriesRepository timeSeriesRepository) {
        this.timeSeriesRepository = timeSeriesRepository;
    }

    public void store(final MqttMessage message) {
        timeSeriesRepository.recordData(message.topic(), message.value());
    }
}
