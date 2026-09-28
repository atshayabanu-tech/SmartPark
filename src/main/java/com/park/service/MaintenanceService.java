package com.park.service;

import com.park.exception.ResourceNotFoundException;
import com.park.model.IrrigationZone;
import com.park.model.MaintenanceTask;
import com.park.model.Park;
import com.park.repository.IrrigationZoneRepository;
import com.park.repository.ParkRepository;
import com.park.repository.MaintenanceTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class MaintenanceService {

    private final MaintenanceTaskRepository maintenanceTaskRepository;

    private final ParkRepository parkRepository;
    private final IrrigationZoneRepository zoneRepository;

    public MaintenanceService(MaintenanceTaskRepository maintenanceTaskRepository, ParkRepository parkRepository,
                              IrrigationZoneRepository zoneRepository) {
        this.maintenanceTaskRepository = maintenanceTaskRepository;
        this.parkRepository = parkRepository;
        this.zoneRepository = zoneRepository;
    }

    private Park resolvePark(Park ref) {
        if (ref == null || ref.getId() == null) {
            throw new IllegalArgumentException("A park must be selected");
        }
        return parkRepository.findById(ref.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Park not found with id: " + ref.getId()));
    }

    private IrrigationZone resolveZone(IrrigationZone ref) {
        if (ref == null || ref.getId() == null) {
            return null;
        }
        return zoneRepository.findById(ref.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Irrigation zone not found with id: " + ref.getId()));
    }

    public List<MaintenanceTask> findAll() {
        return refreshOverdueStatuses(maintenanceTaskRepository.findAll());
    }

    public List<MaintenanceTask> search(String search, String status, String priority, String category, String assignedTo) {
        List<MaintenanceTask> results;
        if (StringUtils.hasText(search)) {
            results = maintenanceTaskRepository
                    .findByTaskTitleContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrAssignedToContainingIgnoreCaseOrStatusContainingIgnoreCase(
                            search, search, search, search);
        } else {
            results = maintenanceTaskRepository.findAll();
        }

        if (StringUtils.hasText(status)) {
            if ("OVERDUE".equalsIgnoreCase(status)) {
                results = results.stream().filter(MaintenanceTask::isOverdue).toList();
            } else {
                results = results.stream().filter(t -> status.equalsIgnoreCase(t.getStatus())).toList();
            }
        }
        if (StringUtils.hasText(priority)) {
            results = results.stream().filter(t -> priority.equalsIgnoreCase(t.getPriority())).toList();
        }
        if (StringUtils.hasText(category)) {
            results = results.stream().filter(t -> category.equalsIgnoreCase(t.getCategory())).toList();
        }
        if (StringUtils.hasText(assignedTo)) {
            results = results.stream()
                    .filter(t -> t.getAssignedTo() != null && t.getAssignedTo().equalsIgnoreCase(assignedTo))
                    .toList();
        }
        return refreshOverdueStatuses(results);
    }

    private List<MaintenanceTask> refreshOverdueStatuses(List<MaintenanceTask> tasks) {
        // Display-level computation only; does not overwrite COMPLETED/IN_PROGRESS choices explicitly made
        return tasks;
    }

    public MaintenanceTask findById(Long id) {
        return maintenanceTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance task not found with id: " + id));
    }

    public List<MaintenanceTask> findByParkId(Long parkId) {
        return maintenanceTaskRepository.findByParkId(parkId);
    }

    public MaintenanceTask save(MaintenanceTask task) {
        if (task.getStatus() == null) {
            task.setStatus("PENDING");
        }
        task.setPark(resolvePark(task.getPark()));
        task.setZone(resolveZone(task.getZone()));
        if ("COMPLETED".equals(task.getStatus()) && task.getCompletedDate() == null) {
            task.setCompletedDate(LocalDate.now());
        }
        return maintenanceTaskRepository.save(task);
    }

    public MaintenanceTask update(Long id, MaintenanceTask updated) {
        MaintenanceTask existing = findById(id);
        existing.setPark(resolvePark(updated.getPark()));
        existing.setZone(resolveZone(updated.getZone()));
        existing.setTaskTitle(updated.getTaskTitle());
        existing.setDescription(updated.getDescription());
        existing.setCategory(updated.getCategory());
        existing.setPriority(updated.getPriority());
        existing.setAssignedTo(updated.getAssignedTo());
        existing.setScheduledDate(updated.getScheduledDate());
        existing.setStatus(updated.getStatus());
        existing.setNotes(updated.getNotes());
        if ("COMPLETED".equals(existing.getStatus()) && existing.getCompletedDate() == null) {
            existing.setCompletedDate(LocalDate.now());
        }
        return maintenanceTaskRepository.save(existing);
    }

    public void delete(Long id) {
        MaintenanceTask task = findById(id);
        maintenanceTaskRepository.delete(task);
    }

    public MaintenanceTask complete(Long id) {
        MaintenanceTask task = findById(id);
        task.setStatus("COMPLETED");
        task.setCompletedDate(LocalDate.now());
        return maintenanceTaskRepository.save(task);
    }

    public long countPending() {
        return maintenanceTaskRepository.countByStatus("PENDING");
    }

    public long countOverdue() {
        return maintenanceTaskRepository.countByScheduledDateBeforeAndStatusNot(LocalDate.now(), "COMPLETED");
    }
}
