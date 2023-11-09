package org.fermented.dairy.solar.entity.dao;

/**
 * Traffic light temperature regions.
 *
 * @param t1 upper bound temperature of green region, lower bound of yellow region
 * @param t2 upper bound of temperature for yellow region, lower bound for red region
 * @param green ok, value for setting for temperature range, in interval notation (-infinity, t1]
 * @param yellow warning, value for setting for temperature range, in interval notation (t1, t2]
 * @param red danger, value for setting for temperature range, in interval notation (t2, infinity]
 */
public record TemperatureRegions(double t1, double t2, int green, int yellow, int red) {

    /**
     * Returns setting value based on temperature.
     *
     * @param temperature the temperature
     * @return The setting value
     */
    public int getSettingValue(final double temperature) {
        if (temperature <= t1) {
            return green;
        }
        if (temperature <= t2) {
            return yellow;
        }

        return red;
    }
}
