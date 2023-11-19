package org.fermented.dairy.solar.controller.consumer.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.fermented.dairy.solar.controller.repository.TimeSeriesRepository;
import org.fermented.dairy.solar.entity.messaging.DataPoint;

/**
 * Liveness check for Time Data processing.
 * Rule: unhealthy if the latest data point is older than 30s. Under normal operation, messages are produces at least every 30s
 */
@Liveness
@ApplicationScoped
public class TimeDataHealth implements HealthCheck {

    private final TimeSeriesRepository timeSeriesRepository;

    @Inject
    public TimeDataHealth(final TimeSeriesRepository timeSeriesRepository) {
        this.timeSeriesRepository = timeSeriesRepository;
    }

    @Override
    public HealthCheckResponse call() {
        final Optional<DataPoint> optDataPoint = timeSeriesRepository.getLastDatapoint();
        if (optDataPoint.isPresent()
                && optDataPoint.get().offsetDateTime().isBefore(OffsetDateTime.now().minusSeconds(30))) {
            return HealthCheckResponse.down("TimeSeries");
        }
        return HealthCheckResponse.up("TimeSeries");
    }
}
