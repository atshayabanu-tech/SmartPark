package com.park.service;

import com.park.config.IrrigationThresholds;
import com.park.exception.ResourceNotFoundException;
import com.park.model.IrrigationZone;
import com.park.model.SensorReading;
import com.park.repository.IrrigationZoneRepository;
import com.park.repository.SensorReadingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorService {

    private final SensorReadingRepository sensorReadingRepository;
    private final IrrigationZoneRepository zoneRepository;
    private final IrrigationThresholds thresholds;
    private final AlertService alertService;
    private final ZoneService zoneService;
    private final IrrigationService irrigationService;

    public SensorService(SensorReadingRepository sensorReadingRepository,
                          IrrigationZoneRepository zoneRepository,
                          IrrigationThresholds thresholds,
                          AlertService alertService,
                          ZoneService zoneService,
                          IrrigationService irrigationService) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.zoneRepository = zoneRepository;
        this.thresholds = thresholds;
        this.alertService = alertService;
        this.zoneService = zoneService;
        this.irrigationService = irrigationService;
    }

    public List<SensorReading> findAll() {
        return sensorReadingRepository.findTop20ByOrderByRecordedAtDesc();
    }

    public List<SensorReading> findByZoneId(Long zoneId) {
        return sensorReadingRepository.findByZoneIdOrderByRecordedAtDesc(zoneId);
    }

    public SensorReading findById(Long id) {
        return sensorReadingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor reading not found with id: " + id));
    }

    /**
     * Saves a new sensor reading, updates the owning zone's live moisture level,
     * recalculates the zone's rule-based irrigation status, generates relevant
     * alerts (without duplicating existing unresolved ones), and - when the zone
     * needs water and no significant rainfall was recorded - creates an automatic
     * irrigation recommendation.
     */
    public SensorReading recordReading(SensorReading reading) {
        if (reading.getZone() == null || reading.getZone().getId() == null) {
            throw new IllegalArgumentException("A zone must be selected for the sensor reading");
        }
        IrrigationZone zone = zoneRepository.findById(reading.getZone().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Irrigation zone not found with id: " + reading.getZone().getId()));
        reading.setZone(zone);
        SensorReading saved = sensorReadingRepository.save(reading);

        if (saved.getMoistureLevel() != null) {
            zone.setMoistureLevel(saved.getMoistureLevel());
        }
        zoneService.recalculateIrrigationStatus(zone);
        zoneRepository.save(zone);

        double moistureThreshold = zone.getMoistureThreshold() != null ? zone.getMoistureThreshold() : thresholds.getDefaultMoistureThreshold();

        if (saved.getMoistureLevel() != null && saved.getMoistureLevel() < moistureThreshold) {
            alertService.createIfNotDuplicate(zone, "LOW_SOIL_MOISTURE",
                    "Soil moisture (" + saved.getMoistureLevel() + "%) in zone " + zone.getZoneCode()
                            + " has dropped below the threshold (" + moistureThreshold + "%).",
                    "HIGH");
        }

        if (saved.getTemperature() != null && saved.getTemperature() >= thresholds.getHighTemperatureThresholdC()) {
            alertService.createIfNotDuplicate(zone, "HIGH_TEMPERATURE",
                    "Temperature (" + saved.getTemperature() + "\u00b0C) in zone " + zone.getZoneCode()
                            + " has exceeded the configured threshold (" + thresholds.getHighTemperatureThresholdC() + "\u00b0C).",
                    "MEDIUM");
        }

        if (saved.getRainfall() != null && saved.getRainfall() >= thresholds.getRainfallThresholdMm()) {
            alertService.createIfNotDuplicate(zone, "HEAVY_RAINFALL",
                    "Rainfall (" + saved.getRainfall() + "mm) in zone " + zone.getZoneCode()
                            + " exceeds the configured threshold (" + thresholds.getRainfallThresholdMm() + "mm). Automatic watering may be skipped.",
                    "LOW");
        }

        if ("NEEDS_WATER".equals(zone.getIrrigationStatus())) {
            alertService.createIfNotDuplicate(zone, "IRRIGATION_REQUIRED",
                    "Zone " + zone.getZoneCode() + " requires irrigation \u2014 field verification recommended.",
                    "MEDIUM");
            irrigationService.createAutomaticRecommendationIfNeeded(zone, thresholds.getRainfallThresholdMm(), saved.getRainfall());
        }

        return saved;
    }
}
