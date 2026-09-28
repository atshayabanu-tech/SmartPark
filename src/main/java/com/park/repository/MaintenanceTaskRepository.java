package com.park.repository;

import com.park.model.MaintenanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Long> {

    List<MaintenanceTask> findByParkId(Long parkId);

    List<MaintenanceTask> findByZoneId(Long zoneId);

    List<MaintenanceTask> findByStatus(String status);

    List<MaintenanceTask> findByPriority(String priority);

    List<MaintenanceTask> findByCategory(String category);

    List<MaintenanceTask> findByAssignedTo(String assignedTo);

    long countByStatus(String status);

    List<MaintenanceTask> findByScheduledDateBeforeAndStatusNot(LocalDate date, String status);

    long countByScheduledDateBeforeAndStatusNot(LocalDate date, String status);

    List<MaintenanceTask> findByTaskTitleContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrAssignedToContainingIgnoreCaseOrStatusContainingIgnoreCase(
            String taskTitle, String category, String assignedTo, String status);
}
