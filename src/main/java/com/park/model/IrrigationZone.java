package com.park.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "irrigation_zones")
public class IrrigationZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Zone code is required")
    @Column(unique = true, nullable = false)
    private String zoneCode;

    @NotBlank(message = "Zone name is required")
    private String zoneName;

    @NotNull(message = "Park is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "park_id")
    private Park park;

    @PositiveOrZero(message = "Area cannot be negative")
    private Double area;

    private String plantType;

    private String soilType;

    @PositiveOrZero(message = "Moisture level cannot be negative")
    private Double moistureLevel = 0.0;

    @PositiveOrZero(message = "Moisture threshold cannot be negative")
    private Double moistureThreshold = 40.0;

    @PositiveOrZero(message = "Water requirement cannot be negative")
    private Double waterRequirement;

    // OPEN / CLOSED
    private String valveStatus = "CLOSED";

    // NORMAL / NEEDS_WATER / RUNNING / COMPLETED / SCHEDULED / SKIPPED / CANCELLED
    private String irrigationStatus = "NORMAL";

    private LocalDateTime lastWatered;

    private LocalDateTime nextScheduledWatering;

    @Column(length = 1000)
    private String notes;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<IrrigationSchedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SensorReading> sensorReadings = new ArrayList<>();

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Alert> alerts = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getZoneCode() { return zoneCode; }
    public void setZoneCode(String zoneCode) { this.zoneCode = zoneCode; }

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }

    public Park getPark() { return park; }
    public void setPark(Park park) { this.park = park; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public String getPlantType() { return plantType; }
    public void setPlantType(String plantType) { this.plantType = plantType; }

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public Double getMoistureLevel() { return moistureLevel; }
    public void setMoistureLevel(Double moistureLevel) { this.moistureLevel = moistureLevel; }

    public Double getMoistureThreshold() { return moistureThreshold; }
    public void setMoistureThreshold(Double moistureThreshold) { this.moistureThreshold = moistureThreshold; }

    public Double getWaterRequirement() { return waterRequirement; }
    public void setWaterRequirement(Double waterRequirement) { this.waterRequirement = waterRequirement; }

    public String getValveStatus() { return valveStatus; }
    public void setValveStatus(String valveStatus) { this.valveStatus = valveStatus; }

    public String getIrrigationStatus() { return irrigationStatus; }
    public void setIrrigationStatus(String irrigationStatus) { this.irrigationStatus = irrigationStatus; }

    public LocalDateTime getLastWatered() { return lastWatered; }
    public void setLastWatered(LocalDateTime lastWatered) { this.lastWatered = lastWatered; }

    public LocalDateTime getNextScheduledWatering() { return nextScheduledWatering; }
    public void setNextScheduledWatering(LocalDateTime nextScheduledWatering) { this.nextScheduledWatering = nextScheduledWatering; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<IrrigationSchedule> getSchedules() { return schedules; }
    public void setSchedules(List<IrrigationSchedule> schedules) { this.schedules = schedules; }

    public List<SensorReading> getSensorReadings() { return sensorReadings; }
    public void setSensorReadings(List<SensorReading> sensorReadings) { this.sensorReadings = sensorReadings; }

    public List<Alert> getAlerts() { return alerts; }
    public void setAlerts(List<Alert> alerts) { this.alerts = alerts; }
}
