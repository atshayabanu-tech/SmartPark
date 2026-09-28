package com.park.repository;

import com.park.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    List<SensorReading> findByZoneId(Long zoneId);

    List<SensorReading> findTop20ByOrderByRecordedAtDesc();

    List<SensorReading> findByZoneIdOrderByRecordedAtDesc(Long zoneId);

    List<SensorReading> findTop10ByZonePark_IdOrderByRecordedAtDesc(Long parkId);
}
