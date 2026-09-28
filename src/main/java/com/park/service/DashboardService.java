package com.park.service;

import com.park.model.SensorReading;
import com.park.repository.SensorReadingRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final ParkService parkService;
    private final ZoneService zoneService;
    private final MaintenanceService maintenanceService;
    private final IrrigationService irrigationService;
    private final AlertService alertService;
    private final SensorReadingRepository sensorReadingRepository;

    public DashboardService(ParkService parkService, ZoneService zoneService, MaintenanceService maintenanceService,
                             IrrigationService irrigationService, AlertService alertService,
                             SensorReadingRepository sensorReadingRepository) {
        this.parkService = parkService;
        this.zoneService = zoneService;
        this.maintenanceService = maintenanceService;
        this.irrigationService = irrigationService;
        this.alertService = alertService;
        this.sensorReadingRepository = sensorReadingRepository;
    }

    public Map<String, Object> buildStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalParks", parkService.countAll());
        stats.put("totalZones", zoneService.countAll());
        stats.put("zonesRequiringWater", zoneService.countRequiringWater());
        stats.put("activeIrrigationZones", zoneService.countActiveIrrigation());
        stats.put("pendingMaintenance", maintenanceService.countPending());
        stats.put("overdueMaintenance", maintenanceService.countOverdue());
        stats.put("todaysSchedules", irrigationService.countToday());
        stats.put("activeAlerts", alertService.countActive());
        stats.put("averageMoisture", Math.round(zoneService.averageMoisture() * 10.0) / 10.0);
        return stats;
    }

    public List<SensorReading> recentReadings() {
        return sensorReadingRepository.findTop20ByOrderByRecordedAtDesc();
    }
}
