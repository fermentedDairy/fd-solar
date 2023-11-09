package org.fermented.dairy.solar.entity.messaging;

/**
 * Record modelling mqtt messages. These messages are essentially key-value pairs where the key is the topic.
 *
 * @param topic The topic
 * @param value The value
 */
public record MqttMessage(String topic, String value) {
}
