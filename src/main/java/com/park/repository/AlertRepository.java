package com.park.repository;

import com.park.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByResolvedFalseOrderByAlertDateDesc();

    List<Alert> findByResolvedTrueOrderByAlertDateDesc();

    List<Alert> findByParkId(Long parkId);

    List<Alert> findByZoneId(Long zoneId);

    List<Alert> findBySeverity(String severity);

    List<Alert> findByType(String type);

    long countByResolvedFalse();

    List<Alert> findByZoneIdAndTypeAndResolvedFalse(Long zoneId, String type);

    List<Alert> findAllByOrderByAlertDateDesc();
}
