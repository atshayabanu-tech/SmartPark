package com.park.config;

import com.park.model.*;
import com.park.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ParkRepository parkRepository;
    private final IrrigationZoneRepository zoneRepository;
    private final IrrigationScheduleRepository scheduleRepository;
    private final MaintenanceTaskRepository taskRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final AlertRepository alertRepository;

    public DataInitializer(ParkRepository parkRepository, IrrigationZoneRepository zoneRepository,
                            IrrigationScheduleRepository scheduleRepository, MaintenanceTaskRepository taskRepository,
                            SensorReadingRepository sensorReadingRepository, AlertRepository alertRepository) {
        this.parkRepository = parkRepository;
        this.zoneRepository = zoneRepository;
        this.scheduleRepository = scheduleRepository;
        this.taskRepository = taskRepository;
        this.sensorReadingRepository = sensorReadingRepository;
        this.alertRepository = alertRepository;
    }

    @Override
    public void run(String... args) {
        if (parkRepository.count() > 0) {
            // Sample data already present - do not insert again on restart.
            return;
        }

        Map<String, Park> parks = new HashMap<>();

        parks.put("PK-001", createPark("PK-001", "Central City Park", "Main Road, Tiruchirappalli", 12.5,
                "Kumar", "9840011122", LocalTime.of(6, 0), LocalTime.of(20, 0), 6, "ACTIVE",
                "Largest park in the city, popular for morning walks."));
        parks.put("PK-002", createPark("PK-002", "Green Valley Park", "Valley Road", 8.2,
                "Priya Raman", "9840022233", LocalTime.of(6, 0), LocalTime.of(19, 30), 5, "ACTIVE",
                "Known for its rose garden and walking trails."));
        parks.put("PK-003", createPark("PK-003", "Riverside Park", "River Bank Street", 15.0,
                "Suresh Babu", "9840033344", LocalTime.of(5, 30), LocalTime.of(20, 30), 5, "ACTIVE",
                "Riverside park with scenic irrigation zones."));
        parks.put("PK-004", createPark("PK-004", "Children's Garden", "Lakeview Colony", 4.5,
                "Divya Menon", "9840044455", LocalTime.of(7, 0), LocalTime.of(19, 0), 4, "ACTIVE",
                "Dedicated play area with a small garden."));
        parks.put("PK-005", createPark("PK-005", "Community Eco Park", "Eco Nagar", 10.0,
                "Arjun Nair", "9840055566", LocalTime.of(6, 0), LocalTime.of(20, 0), 4, "MAINTENANCE",
                "Eco-friendly park undergoing seasonal upgrades."));

        parkRepository.saveAll(parks.values());

        Map<String, IrrigationZone> zones = new HashMap<>();
        zones.put("Z-001", createZone("Z-001", "Rose Garden", parks.get("PK-001"), 1.2, "Roses", "Loamy", 55.0, 40.0, 300.0, "CLOSED", "NORMAL"));
        zones.put("Z-002", createZone("Z-002", "Main Lawn", parks.get("PK-001"), 3.0, "Grass", "Sandy Loam", 30.0, 40.0, 500.0, "CLOSED", "NEEDS_WATER"));
        zones.put("Z-003", createZone("Z-003", "Children's Garden", parks.get("PK-004"), 0.8, "Ornamental Shrubs", "Loamy", 48.0, 35.0, 250.0, "CLOSED", "NORMAL"));
        zones.put("Z-004", createZone("Z-004", "Tree Area", parks.get("PK-003"), 4.5, "Shade Trees", "Clay", 60.0, 30.0, 400.0, "CLOSED", "NORMAL"));
        zones.put("Z-005", createZone("Z-005", "Entrance Lawn", parks.get("PK-002"), 1.5, "Grass", "Sandy", 25.0, 40.0, 350.0, "CLOSED", "NEEDS_WATER"));
        zones.put("Z-006", createZone("Z-006", "Herb Garden", parks.get("PK-002"), 0.6, "Herbs", "Loamy", 52.0, 38.0, 180.0, "CLOSED", "NORMAL"));
        zones.put("Z-007", createZone("Z-007", "Riverside Trail Bed", parks.get("PK-003"), 2.2, "Wildflowers", "Silty", 44.0, 35.0, 220.0, "CLOSED", "NORMAL"));
        zones.put("Z-008", createZone("Z-008", "Play Area Green", parks.get("PK-004"), 1.0, "Grass", "Sandy Loam", 33.0, 40.0, 300.0, "OPEN", "RUNNING"));
        zones.put("Z-009", createZone("Z-009", "Eco Wetland Zone", parks.get("PK-005"), 3.5, "Wetland Plants", "Clay", 65.0, 45.0, 500.0, "CLOSED", "NORMAL"));
        zones.put("Z-010", createZone("Z-010", "Community Vegetable Patch", parks.get("PK-005"), 1.8, "Vegetables", "Loamy", 28.0, 42.0, 400.0, "CLOSED", "NEEDS_WATER"));

        zoneRepository.saveAll(zones.values());

        LocalDate today = LocalDate.now();

        createSchedule(zones.get("Z-001"), today.minusDays(1), LocalTime.of(6, 30), 20, 300.0, "COMPLETED", true);
        createSchedule(zones.get("Z-002"), today, LocalTime.of(7, 0), 30, 500.0, "SCHEDULED", true);
        createSchedule(zones.get("Z-003"), today, LocalTime.of(6, 0), 15, 250.0, "SCHEDULED", false);
        createSchedule(zones.get("Z-004"), today.minusDays(2), LocalTime.of(6, 30), 25, 400.0, "COMPLETED", false);
        createSchedule(zones.get("Z-005"), today, LocalTime.of(7, 30), 20, 350.0, "SCHEDULED", true);
        createSchedule(zones.get("Z-006"), today.minusDays(1), LocalTime.of(6, 0), 10, 180.0, "SKIPPED", true);
        createSchedule(zones.get("Z-008"), today, LocalTime.of(8, 0), 20, 300.0, "RUNNING", true);
        createSchedule(zones.get("Z-010"), today, LocalTime.of(9, 0), 25, 400.0, "SCHEDULED", true);

        createTask(parks.get("PK-001"), zones.get("Z-002"), "Mow Main Lawn", "Routine grass cutting for the main lawn area.",
                "GRASS_CUTTING", "MEDIUM", "Ravi (Gardener)", today, "PENDING");
        createTask(parks.get("PK-001"), zones.get("Z-001"), "Trim Rose Bushes", "Seasonal trimming of rose garden bushes.",
                "TREE_TRIMMING", "LOW", "Ravi (Gardener)", today.plusDays(2), "PENDING");
        createTask(parks.get("PK-002"), null, "Clean Entrance Pathway", "Sweep and clean the entrance walking path.",
                "CLEANING", "MEDIUM", "Mani (Cleaner)", today.minusDays(3), "OVERDUE");
        createTask(parks.get("PK-002"), null, "Collect Waste Bins", "Empty and clean all waste collection bins.",
                "WASTE_COLLECTION", "HIGH", "Mani (Cleaner)", today.minusDays(1), "IN_PROGRESS");
        createTask(parks.get("PK-003"), zones.get("Z-004"), "Repair Benches Near River Trail", "Two benches have loose planks.",
                "BENCH_REPAIR", "HIGH", "Suresh Babu", today.minusDays(5), "OVERDUE");
        createTask(parks.get("PK-003"), null, "Fix Broken Street Light", "Light near the riverside entrance is not working.",
                "LIGHT_REPAIR", "CRITICAL", "Electric Team", today.minusDays(1), "PENDING");
        createTask(parks.get("PK-004"), zones.get("Z-008"), "Repair Playground Slide", "Slide edge needs sanding and repainting.",
                "PLAYGROUND_REPAIR", "HIGH", "Maintenance Staff", today.plusDays(1), "PENDING");
        createTask(parks.get("PK-004"), null, "Plant New Saplings", "Plant 10 new saplings near the play area.",
                "PLANTING", "LOW", "Gardening Team", today.minusDays(10), "COMPLETED");
        createTask(parks.get("PK-005"), zones.get("Z-009"), "General Wetland Zone Inspection", "Routine inspection of the eco wetland zone.",
                "GENERAL", "MEDIUM", "Arjun Nair", today.minusDays(7), "COMPLETED");
        createTask(parks.get("PK-005"), zones.get("Z-010"), "Repair Bench Community Patch", "Bench near the vegetable patch is damaged.",
                "BENCH_REPAIR", "MEDIUM", "Maintenance Staff", today.minusDays(4), "OVERDUE");

        createSensorReading(zones.get("Z-001"), 55.0, 28.0, 60.0, 0.0, LocalDateTime.now().minusHours(6));
        createSensorReading(zones.get("Z-002"), 30.0, 33.0, 45.0, 0.0, LocalDateTime.now().minusHours(5));
        createSensorReading(zones.get("Z-003"), 48.0, 27.0, 55.0, 2.0, LocalDateTime.now().minusHours(5));
        createSensorReading(zones.get("Z-004"), 60.0, 25.0, 65.0, 5.0, LocalDateTime.now().minusHours(4));
        createSensorReading(zones.get("Z-005"), 25.0, 36.0, 40.0, 0.0, LocalDateTime.now().minusHours(4));
        createSensorReading(zones.get("Z-006"), 52.0, 29.0, 58.0, 0.0, LocalDateTime.now().minusHours(3));
        createSensorReading(zones.get("Z-007"), 44.0, 30.0, 50.0, 1.0, LocalDateTime.now().minusHours(3));
        createSensorReading(zones.get("Z-008"), 33.0, 31.0, 48.0, 0.0, LocalDateTime.now().minusHours(2));
        createSensorReading(zones.get("Z-009"), 65.0, 26.0, 70.0, 15.0, LocalDateTime.now().minusHours(2));
        createSensorReading(zones.get("Z-010"), 28.0, 34.0, 42.0, 0.0, LocalDateTime.now().minusHours(1));
        createSensorReading(zones.get("Z-002"), 29.0, 37.0, 40.0, 0.0, LocalDateTime.now().minusMinutes(50));
        createSensorReading(zones.get("Z-005"), 24.0, 38.0, 38.0, 0.0, LocalDateTime.now().minusMinutes(40));
        createSensorReading(zones.get("Z-001"), 54.0, 29.0, 61.0, 0.0, LocalDateTime.now().minusMinutes(30));
        createSensorReading(zones.get("Z-009"), 66.0, 24.0, 72.0, 18.0, LocalDateTime.now().minusMinutes(20));
        createSensorReading(zones.get("Z-010"), 27.0, 33.0, 41.0, 0.0, LocalDateTime.now().minusMinutes(10));

        createAlert(parks.get("PK-001"), zones.get("Z-002"), "LOW_SOIL_MOISTURE",
                "Soil moisture (29.0%) in zone Z-002 has dropped below the threshold (40.0%).", "HIGH", false);
        createAlert(parks.get("PK-002"), zones.get("Z-005"), "LOW_SOIL_MOISTURE",
                "Soil moisture (24.0%) in zone Z-005 has dropped below the threshold (40.0%).", "HIGH", false);
        createAlert(parks.get("PK-002"), zones.get("Z-005"), "HIGH_TEMPERATURE",
                "Temperature (38.0\u00b0C) in zone Z-005 has exceeded the configured threshold (35.0\u00b0C).", "MEDIUM", false);
        createAlert(parks.get("PK-005"), zones.get("Z-009"), "HEAVY_RAINFALL",
                "Rainfall (18.0mm) in zone Z-009 exceeds the configured threshold (10.0mm). Automatic watering may be skipped.", "LOW", false);
        createAlert(parks.get("PK-001"), zones.get("Z-002"), "IRRIGATION_REQUIRED",
                "Zone Z-002 requires irrigation \u2014 field verification recommended.", "MEDIUM", false);
        createAlert(parks.get("PK-003"), null, "MAINTENANCE_OVERDUE",
                "Task 'Repair Benches Near River Trail' at Riverside Park is overdue.", "CRITICAL", false);
        createAlert(parks.get("PK-005"), zones.get("Z-010"), "MAINTENANCE_OVERDUE",
                "Task 'Repair Bench Community Patch' at Community Eco Park is overdue.", "HIGH", true);
    }

    private Park createPark(String code, String name, String location, double area, String manager, String contact,
                             LocalTime open, LocalTime close, int totalZones, String status, String notes) {
        Park park = new Park();
        park.setParkCode(code);
        park.setParkName(name);
        park.setLocation(location);
        park.setArea(area);
        park.setManagerName(manager);
        park.setContactNumber(contact);
        park.setOpeningTime(open);
        park.setClosingTime(close);
        park.setTotalZones(totalZones);
        park.setStatus(status);
        park.setNotes(notes);
        return park;
    }

    private IrrigationZone createZone(String code, String name, Park park, double area, String plantType, String soilType,
                                       double moisture, double threshold, double waterRequirement, String valveStatus, String irrigationStatus) {
        IrrigationZone zone = new IrrigationZone();
        zone.setZoneCode(code);
        zone.setZoneName(name);
        zone.setPark(park);
        zone.setArea(area);
        zone.setPlantType(plantType);
        zone.setSoilType(soilType);
        zone.setMoistureLevel(moisture);
        zone.setMoistureThreshold(threshold);
        zone.setWaterRequirement(waterRequirement);
        zone.setValveStatus(valveStatus);
        zone.setIrrigationStatus(irrigationStatus);
        zone.setLastWatered(LocalDateTime.now().minusDays(1));
        return zone;
    }

    private void createSchedule(IrrigationZone zone, LocalDate date, LocalTime time, int duration, double quantity, String status, boolean automatic) {
        IrrigationSchedule schedule = new IrrigationSchedule();
        schedule.setZone(zone);
        schedule.setScheduledDate(date);
        schedule.setStartTime(time);
        schedule.setDurationMinutes(duration);
        schedule.setWaterQuantity(quantity);
        schedule.setStatus(status);
        schedule.setAutomatic(automatic);
        schedule.setNotes(automatic ? "Automated irrigation recommendation \u2014 field verification recommended." : "Manually scheduled by staff.");
        scheduleRepository.save(schedule);
    }

    private void createTask(Park park, IrrigationZone zone, String title, String description, String category,
                             String priority, String assignedTo, LocalDate scheduledDate, String status) {
        MaintenanceTask task = new MaintenanceTask();
        task.setPark(park);
        task.setZone(zone);
        task.setTaskTitle(title);
        task.setDescription(description);
        task.setCategory(category);
        task.setPriority(priority);
        task.setAssignedTo(assignedTo);
        task.setScheduledDate(scheduledDate);
        task.setStatus(status);
        if ("COMPLETED".equals(status)) {
            task.setCompletedDate(scheduledDate.plusDays(1));
        }
        taskRepository.save(task);
    }

    private void createSensorReading(IrrigationZone zone, double moisture, double temperature, double humidity, double rainfall, LocalDateTime recordedAt) {
        SensorReading reading = new SensorReading();
        reading.setZone(zone);
        reading.setMoistureLevel(moisture);
        reading.setTemperature(temperature);
        reading.setHumidity(humidity);
        reading.setRainfall(rainfall);
        reading.setRecordedAt(recordedAt);
        sensorReadingRepository.save(reading);
    }

    private void createAlert(Park park, IrrigationZone zone, String type, String message, String severity, boolean resolved) {
        Alert alert = new Alert();
        alert.setPark(park);
        alert.setZone(zone);
        alert.setType(type);
        alert.setMessage(message);
        alert.setSeverity(severity);
        alert.setResolved(resolved);
        alertRepository.save(alert);
    }
}
