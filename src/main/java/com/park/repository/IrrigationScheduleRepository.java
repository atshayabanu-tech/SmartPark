package com.park.repository;

import com.park.model.IrrigationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IrrigationScheduleRepository extends JpaRepository<IrrigationSchedule, Long> {

    List<IrrigationSchedule> findByZoneId(Long zoneId);

    List<IrrigationSchedule> findByStatus(String status);

    List<IrrigationSchedule> findByScheduledDate(LocalDate scheduledDate);

    long countByScheduledDateAndStatus(LocalDate scheduledDate, String status);

    long countByScheduledDate(LocalDate scheduledDate);

    List<IrrigationSchedule> findByZoneIdAndStatusIn(Long zoneId, List<String> statuses);
}
