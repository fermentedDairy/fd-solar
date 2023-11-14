package org.fermented.dairy.solar.entity.messaging;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * Record modeling a time series data point.
 *
 * @param offsetDateTime date and time of the event with timezone
 * @param topic mqtt topic
 * @param value point value
 */
public record DataPoint(OffsetDateTime offsetDateTime, String topic, String value) {
    public DataPoint(final String topic, final String value) {
        this(OffsetDateTime.now(), topic, value);
    }
}
