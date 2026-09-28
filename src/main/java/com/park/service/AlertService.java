package com.park.service;

import com.park.exception.ResourceNotFoundException;
import com.park.model.Alert;
import com.park.model.IrrigationZone;
import com.park.repository.AlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public List<Alert> findActive() {
        return alertRepository.findByResolvedFalseOrderByAlertDateDesc();
    }

    public List<Alert> findResolved() {
        return alertRepository.findByResolvedTrueOrderByAlertDateDesc();
    }

    public List<Alert> findAllOrdered() {
        return alertRepository.findAllByOrderByAlertDateDesc();
    }

    public List<Alert> filter(String severity, String type, String resolvedFilter) {
        List<Alert> results = alertRepository.findAllByOrderByAlertDateDesc();

        if (StringUtils.hasText(severity)) {
            results = results.stream().filter(a -> severity.equalsIgnoreCase(a.getSeverity())).toList();
        }
        if (StringUtils.hasText(type)) {
            results = results.stream().filter(a -> type.equalsIgnoreCase(a.getType())).toList();
        }
        if (StringUtils.hasText(resolvedFilter)) {
            boolean wantResolved = "resolved".equalsIgnoreCase(resolvedFilter);
            results = results.stream().filter(a -> a.getResolved() == wantResolved).toList();
        }
        return results;
    }

    public Alert findById(Long id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + id));
    }

    public Alert resolve(Long id) {
        Alert alert = findById(id);
        alert.setResolved(true);
        return alertRepository.save(alert);
    }

    public long countActive() {
        return alertRepository.countByResolvedFalse();
    }

    /**
     * Creates an alert for the given zone/type only if an unresolved alert of the
     * same type does not already exist for that zone, to avoid duplicate noise.
     */
    public Alert createIfNotDuplicate(IrrigationZone zone, String type, String message, String severity) {
        List<Alert> existing = alertRepository.findByZoneIdAndTypeAndResolvedFalse(zone.getId(), type);
        if (!existing.isEmpty()) {
            return null;
        }
        Alert alert = new Alert();
        alert.setPark(zone.getPark());
        alert.setZone(zone);
        alert.setType(type);
        alert.setMessage(message);
        alert.setSeverity(severity);
        alert.setResolved(false);
        return alertRepository.save(alert);
    }

    public Alert save(Alert alert) {
        return alertRepository.save(alert);
    }
}
