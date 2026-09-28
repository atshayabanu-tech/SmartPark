package com.park.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "irrigation_schedules")
public class IrrigationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Zone is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private IrrigationZone zone;

    @NotNull(message = "Scheduled date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate scheduledDate;

    @NotNull(message = "Start time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime;

    @Positive(message = "Duration must be positive")
    private Integer durationMinutes;

    @Positive(message = "Water quantity must be positive")
    private Double waterQuantity;

    // SCHEDULED, RUNNING, COMPLETED, SKIPPED, CANCELLED
    private String status = "SCHEDULED";

    private Boolean automatic = false;

    @Column(length = 1000)
    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public IrrigationZone getZone() { return zone; }
    public void setZone(IrrigationZone zone) { this.zone = zone; }

    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Double getWaterQuantity() { return waterQuantity; }
    public void setWaterQuantity(Double waterQuantity) { this.waterQuantity = waterQuantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getAutomatic() { return automatic; }
    public void setAutomatic(Boolean automatic) { this.automatic = automatic; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
