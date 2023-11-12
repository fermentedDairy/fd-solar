package org.fermented.dairy.solar.controller.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.fermented.dairy.solar.controller.repository.TimeSeriesRepository;

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

    public void store(final String topic, final String value) {
        timeSeriesRepository.recordData(topic, value);
    }
}
