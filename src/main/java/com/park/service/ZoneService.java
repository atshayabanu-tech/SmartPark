package com.park.service;

import com.park.config.IrrigationThresholds;
import com.park.exception.ResourceNotFoundException;
import com.park.model.IrrigationZone;
import com.park.model.MaintenanceTask;
import com.park.model.Park;
import com.park.repository.IrrigationZoneRepository;
import com.park.repository.MaintenanceTaskRepository;
import com.park.repository.ParkRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ZoneService {

    private final IrrigationZoneRepository zoneRepository;
    private final IrrigationThresholds thresholds;
    private final ParkRepository parkRepository;
    private final MaintenanceTaskRepository taskRepository;

    public ZoneService(IrrigationZoneRepository zoneRepository, IrrigationThresholds thresholds,
                       ParkRepository parkRepository, MaintenanceTaskRepository taskRepository) {
        this.zoneRepository = zoneRepository;
        this.thresholds = thresholds;
        this.parkRepository = parkRepository;
        this.taskRepository = taskRepository;
    }

    private Park resolvePark(Park ref) {
        if (ref == null || ref.getId() == null) {
            throw new IllegalArgumentException("A park must be selected");
        }
        return parkRepository.findById(ref.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Park not found with id: " + ref.getId()));
    }

    public List<IrrigationZone> findAll() {
        return zoneRepository.findAll();
    }

    public List<IrrigationZone> search(String search, Long parkId, String irrigationStatus, String plantType, String soilType) {
        List<IrrigationZone> results;
        if (StringUtils.hasText(search)) {
            results = zoneRepository
                    .findByZoneNameContainingIgnoreCaseOrZoneCodeContainingIgnoreCaseOrPlantTypeContainingIgnoreCaseOrSoilTypeContainingIgnoreCase(
                            search, search, search, search);
        } else {
            results = zoneRepository.findAll();
        }

        if (parkId != null) {
            results = results.stream().filter(z -> z.getPark() != null && z.getPark().getId().equals(parkId)).toList();
        }
        if (StringUtils.hasText(irrigationStatus)) {
            results = results.stream().filter(z -> irrigationStatus.equalsIgnoreCase(z.getIrrigationStatus())).toList();
        }
        if (StringUtils.hasText(plantType)) {
            results = results.stream()
                    .filter(z -> z.getPlantType() != null && z.getPlantType().equalsIgnoreCase(plantType))
                    .toList();
        }
        if (StringUtils.hasText(soilType)) {
            results = results.stream()
                    .filter(z -> z.getSoilType() != null && z.getSoilType().equalsIgnoreCase(soilType))
                    .toList();
        }
        return results;
    }

    public IrrigationZone findById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Irrigation zone not found with id: " + id));
    }

    public List<IrrigationZone> findByParkId(Long parkId) {
        return zoneRepository.findByParkId(parkId);
    }

    public IrrigationZone save(IrrigationZone zone) {
        if (zone.getId() == null && zoneRepository.existsByZoneCode(zone.getZoneCode())) {
            throw new IllegalArgumentException("Zone code '" + zone.getZoneCode() + "' already exists");
        }
        zone.setPark(resolvePark(zone.getPark()));
        if (zone.getMoistureLevel() == null) {
            zone.setMoistureLevel(0.0);
        }
        if (zone.getMoistureThreshold() == null) {
            zone.setMoistureThreshold(thresholds.getDefaultMoistureThreshold());
        }
        recalculateIrrigationStatus(zone);
        return zoneRepository.save(zone);
    }

    public IrrigationZone update(Long id, IrrigationZone updated) {
        IrrigationZone existing = findById(id);
        zoneRepository.findByZoneCode(updated.getZoneCode()).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new IllegalArgumentException("Zone code '" + updated.getZoneCode() + "' already exists");
            }
        });
        existing.setZoneCode(updated.getZoneCode());
        existing.setZoneName(updated.getZoneName());
        existing.setPark(resolvePark(updated.getPark()));
        existing.setArea(updated.getArea());
        existing.setPlantType(updated.getPlantType());
        existing.setSoilType(updated.getSoilType());
        existing.setMoistureLevel(updated.getMoistureLevel());
        existing.setMoistureThreshold(updated.getMoistureThreshold());
        existing.setWaterRequirement(updated.getWaterRequirement());
        existing.setNotes(updated.getNotes());
        recalculateIrrigationStatus(existing);
        return zoneRepository.save(existing);
    }

    public void delete(Long id) {
        IrrigationZone zone = findById(id);
        for (MaintenanceTask t : taskRepository.findByZoneId(id)) {
            t.setZone(null);
            taskRepository.save(t);
        }
        zoneRepository.delete(zone);
    }

    /**
     * Transparent, rule-based recalculation of a zone's irrigation status.
     * This is NOT an AI/ML prediction - it is a simple threshold comparison,
     * displayed to the user as a recommendation requiring field verification.
     */
    public void recalculateIrrigationStatus(IrrigationZone zone) {
        if ("RUNNING".equals(zone.getIrrigationStatus())) {
            // Don't override an active irrigation run
            return;
        }
        Double moisture = zone.getMoistureLevel();
        Double threshold = zone.getMoistureThreshold() != null ? zone.getMoistureThreshold() : thresholds.getDefaultMoistureThreshold();
        if (moisture != null && moisture < threshold) {
            zone.setIrrigationStatus("NEEDS_WATER");
        } else if (!"SCHEDULED".equals(zone.getIrrigationStatus())) {
            zone.setIrrigationStatus("NORMAL");
        }
    }

    public long countAll() {
        return zoneRepository.count();
    }

    public long countRequiringWater() {
        return zoneRepository.findAll().stream()
                .filter(z -> z.getMoistureLevel() != null && z.getMoistureThreshold() != null
                        && z.getMoistureLevel() < z.getMoistureThreshold())
                .count();
    }

    public long countActiveIrrigation() {
        return zoneRepository.countByIrrigationStatus("RUNNING");
    }

    public double averageMoisture() {
        List<IrrigationZone> zones = zoneRepository.findAll();
        return zones.stream()
                .filter(z -> z.getMoistureLevel() != null)
                .mapToDouble(IrrigationZone::getMoistureLevel)
                .average()
                .orElse(0.0);
    }
}
