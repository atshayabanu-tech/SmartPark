package com.park.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Centralized, configurable thresholds used by the rule-based (non-AI)
 * irrigation recommendation engine and the sensor alert generator.
 * Keeping these values here (instead of scattered across controllers)
 * makes the automatic irrigation/alerting logic transparent and easy to tune.
 */
@Component
public class IrrigationThresholds {

    @Value("${park.threshold.rainfall:10.0}")
    private double rainfallThresholdMm;

    @Value("${park.threshold.temperature:35.0}")
    private double highTemperatureThresholdC;

    @Value("${park.threshold.default-moisture:40.0}")
    private double defaultMoistureThreshold;

    public double getRainfallThresholdMm() {
        return rainfallThresholdMm;
    }

    public double getHighTemperatureThresholdC() {
        return highTemperatureThresholdC;
    }

    public double getDefaultMoistureThreshold() {
        return defaultMoistureThreshold;
    }
}
