package com.park.service;

import com.park.exception.ResourceNotFoundException;
import com.park.model.IrrigationSchedule;
import com.park.model.IrrigationZone;
import com.park.repository.IrrigationScheduleRepository;
import com.park.repository.IrrigationZoneRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class IrrigationService {

    private final IrrigationScheduleRepository scheduleRepository;
    private final IrrigationZoneRepository zoneRepository;

    public IrrigationService(IrrigationScheduleRepository scheduleRepository, IrrigationZoneRepository zoneRepository) {
        this.scheduleRepository = scheduleRepository;
        this.zoneRepository = zoneRepository;
    }

    public List<IrrigationSchedule> findAll() {
        return scheduleRepository.findAll();
    }

    public IrrigationSchedule findById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Irrigation schedule not found with id: " + id));
    }

    public IrrigationSchedule save(IrrigationSchedule schedule) {
        if (schedule.getStatus() == null) {
            schedule.setStatus("SCHEDULED");
        }
        if (schedule.getAutomatic() == null) {
            schedule.setAutomatic(false);
        }
        schedule.setZone(resolveZone(schedule.getZone()));
        IrrigationSchedule saved = scheduleRepository.save(schedule);
        IrrigationZone zone = saved.getZone();
        if (zone != null && "SCHEDULED".equals(saved.getStatus())) {
            zone.setNextScheduledWatering(LocalDateTime.of(saved.getScheduledDate(), saved.getStartTime()));
            if (!"RUNNING".equals(zone.getIrrigationStatus())) {
                zone.setIrrigationStatus("SCHEDULED");
            }
            zoneRepository.save(zone);
        }
        return saved;
    }

    public IrrigationSchedule update(Long id, IrrigationSchedule updated) {
        IrrigationSchedule existing = findById(id);
        existing.setZone(resolveZone(updated.getZone()));
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }
        existing.setScheduledDate(updated.getScheduledDate());
        existing.setStartTime(updated.getStartTime());
        existing.setDurationMinutes(updated.getDurationMinutes());
        existing.setWaterQuantity(updated.getWaterQuantity());
        existing.setAutomatic(updated.getAutomatic());
        existing.setNotes(updated.getNotes());
        return scheduleRepository.save(existing);
    }

    private IrrigationZone resolveZone(IrrigationZone ref) {
        if (ref == null || ref.getId() == null) {
            throw new IllegalArgumentException("A zone must be selected");
        }
        return zoneRepository.findById(ref.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Irrigation zone not found with id: " + ref.getId()));
    }

    public void delete(Long id) {
        IrrigationSchedule schedule = findById(id);
        scheduleRepository.delete(schedule);
    }

    public IrrigationSchedule start(Long id) {
        IrrigationSchedule schedule = findById(id);
        schedule.setStatus("RUNNING");
        scheduleRepository.save(schedule);

        IrrigationZone zone = schedule.getZone();
        zone.setValveStatus("OPEN");
        zone.setIrrigationStatus("RUNNING");
        zoneRepository.save(zone);
        return schedule;
    }

    public IrrigationSchedule complete(Long id) {
        IrrigationSchedule schedule = findById(id);
        schedule.setStatus("COMPLETED");
        scheduleRepository.save(schedule);

        IrrigationZone zone = schedule.getZone();
        zone.setValveStatus("CLOSED");
        zone.setIrrigationStatus("NORMAL");
        zone.setLastWatered(LocalDateTime.now());
        // Assume watering restores moisture to a healthy level above threshold
        double threshold = zone.getMoistureThreshold() != null ? zone.getMoistureThreshold() : 40.0;
        zone.setMoistureLevel(Math.max(threshold + 10.0, 60.0));
        zoneRepository.save(zone);
        return schedule;
    }

    public IrrigationSchedule cancel(Long id) {
        IrrigationSchedule schedule = findById(id);
        schedule.setStatus("CANCELLED");
        scheduleRepository.save(schedule);

        IrrigationZone zone = schedule.getZone();
        if (zone != null && "RUNNING".equals(zone.getIrrigationStatus())) {
            zone.setValveStatus("CLOSED");
            zone.setIrrigationStatus("NORMAL");
            zoneRepository.save(zone);
        }
        return schedule;
    }

    public long countToday() {
        return scheduleRepository.countByScheduledDate(LocalDate.now());
    }

    /**
     * Automatic irrigation recommendation engine (rule-based, not AI).
     * If a zone needs water, is not already running, and does not already
     * have a pending SCHEDULED/RUNNING schedule, create a recommended
     * automatic schedule for today.
     */
    public IrrigationSchedule createAutomaticRecommendationIfNeeded(IrrigationZone zone, double rainfallThreshold, Double lastRainfall) {
        if (!"NEEDS_WATER".equals(zone.getIrrigationStatus())) {
            return null;
        }
        if (lastRainfall != null && lastRainfall >= rainfallThreshold) {
            // Heavy rainfall recently recorded - recommend skipping automatic watering
            return null;
        }
        boolean hasPending = !scheduleRepository
                .findByZoneIdAndStatusIn(zone.getId(), Arrays.asList("SCHEDULED", "RUNNING"))
                .isEmpty();
        if (hasPending) {
            return null;
        }
        IrrigationSchedule schedule = new IrrigationSchedule();
        schedule.setZone(zone);
        schedule.setScheduledDate(LocalDate.now());
        schedule.setStartTime(java.time.LocalTime.now().plusMinutes(30));
        schedule.setDurationMinutes(20);
        schedule.setWaterQuantity(zone.getWaterRequirement() != null ? zone.getWaterRequirement() : 300.0);
        schedule.setAutomatic(true);
        schedule.setStatus("SCHEDULED");
        schedule.setNotes("Automated irrigation recommendation \u2014 field verification recommended.");
        return save(schedule);
    }
}
