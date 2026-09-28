package com.park.service;

import com.park.exception.ResourceNotFoundException;
import com.park.model.Park;
import com.park.repository.MaintenanceTaskRepository;
import com.park.repository.ParkRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ParkService {

    private final ParkRepository parkRepository;
    private final MaintenanceTaskRepository taskRepository;

    public ParkService(ParkRepository parkRepository, MaintenanceTaskRepository taskRepository) {
        this.parkRepository = parkRepository;
        this.taskRepository = taskRepository;
    }

    public List<Park> findAll() {
        return parkRepository.findAll();
    }

    public List<Park> search(String search, String status, String location) {
        List<Park> results;
        if (StringUtils.hasText(search)) {
            results = parkRepository
                    .findByParkNameContainingIgnoreCaseOrParkCodeContainingIgnoreCaseOrLocationContainingIgnoreCaseOrManagerNameContainingIgnoreCase(
                            search, search, search, search);
        } else {
            results = parkRepository.findAll();
        }

        if (StringUtils.hasText(status)) {
            results = results.stream().filter(p -> status.equalsIgnoreCase(p.getStatus())).toList();
        }
        if (StringUtils.hasText(location)) {
            results = results.stream()
                    .filter(p -> p.getLocation() != null && p.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .toList();
        }
        return results;
    }

    public Park findById(Long id) {
        return parkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Park not found with id: " + id));
    }

    public Park save(Park park) {
        if (park.getId() == null && parkRepository.existsByParkCode(park.getParkCode())) {
            throw new IllegalArgumentException("Park code '" + park.getParkCode() + "' already exists");
        }
        return parkRepository.save(park);
    }

    public Park update(Long id, Park updated) {
        Park existing = findById(id);
        parkRepository.findByParkCode(updated.getParkCode()).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new IllegalArgumentException("Park code '" + updated.getParkCode() + "' already exists");
            }
        });
        existing.setParkCode(updated.getParkCode());
        existing.setParkName(updated.getParkName());
        existing.setLocation(updated.getLocation());
        existing.setArea(updated.getArea());
        existing.setManagerName(updated.getManagerName());
        existing.setContactNumber(updated.getContactNumber());
        existing.setOpeningTime(updated.getOpeningTime());
        existing.setClosingTime(updated.getClosingTime());
        existing.setTotalZones(updated.getTotalZones());
        existing.setStatus(updated.getStatus());
        existing.setNotes(updated.getNotes());
        return parkRepository.save(existing);
    }

    public void delete(Long id) {
        Park park = findById(id);
        taskRepository.deleteAll(taskRepository.findByParkId(id));
        taskRepository.flush();
        parkRepository.delete(park);
    }

    public long countAll() {
        return parkRepository.count();
    }
}
