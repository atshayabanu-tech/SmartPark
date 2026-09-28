package com.park.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "park_id")
    private Park park;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private IrrigationZone zone;

    // LOW_SOIL_MOISTURE, HIGH_TEMPERATURE, HEAVY_RAINFALL, IRRIGATION_REQUIRED,
    // IRRIGATION_FAILURE, MAINTENANCE_OVERDUE, SENSOR_WARNING
    private String type;

    @Column(length = 1000)
    private String message;

    // LOW, MEDIUM, HIGH, CRITICAL
    private String severity = "MEDIUM";

    private LocalDateTime alertDate;

    private Boolean resolved = false;

    @PrePersist
    protected void onCreate() {
        if (alertDate == null) {
            alertDate = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Park getPark() { return park; }
    public void setPark(Park park) { this.park = park; }

    public IrrigationZone getZone() { return zone; }
    public void setZone(IrrigationZone zone) { this.zone = zone; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public LocalDateTime getAlertDate() { return alertDate; }
    public void setAlertDate(LocalDateTime alertDate) { this.alertDate = alertDate; }

    public Boolean getResolved() { return resolved; }
    public void setResolved(Boolean resolved) { this.resolved = resolved; }
}
