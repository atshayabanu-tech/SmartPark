package com.park.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "maintenance_tasks")
public class MaintenanceTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Park is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "park_id")
    private Park park;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private IrrigationZone zone;

    @NotBlank(message = "Task title is required")
    private String taskTitle;

    @Column(length = 1000)
    private String description;

    // GRASS_CUTTING, TREE_TRIMMING, CLEANING, WASTE_COLLECTION, BENCH_REPAIR,
    // LIGHT_REPAIR, PLAYGROUND_REPAIR, PLANTING, GENERAL
    private String category = "GENERAL";

    // LOW, MEDIUM, HIGH, CRITICAL
    private String priority = "MEDIUM";

    private String assignedTo;

    @NotNull(message = "Scheduled date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate scheduledDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate completedDate;

    // PENDING, IN_PROGRESS, COMPLETED, OVERDUE
    private String status = "PENDING";

    @Column(length = 1000)
    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Park getPark() { return park; }
    public void setPark(Park park) { this.park = park; }

    public IrrigationZone getZone() { return zone; }
    public void setZone(IrrigationZone zone) { this.zone = zone; }

    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }

    public LocalDate getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isOverdue() {
        return scheduledDate != null
                && scheduledDate.isBefore(LocalDate.now())
                && !"COMPLETED".equals(status);
    }
}
