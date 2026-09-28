package com.park.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parks")
public class Park {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Park code is required")
    @Column(unique = true, nullable = false)
    private String parkCode;

    @NotBlank(message = "Park name is required")
    private String parkName;

    @NotBlank(message = "Location is required")
    private String location;

    @PositiveOrZero(message = "Area cannot be negative")
    private Double area;

    private String managerName;

    @Pattern(regexp = "^$|^[0-9+\\-\\s]{7,15}$", message = "Contact number must be 7-15 digits (may include + - or spaces)")
    private String contactNumber;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime openingTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime closingTime;

    @PositiveOrZero(message = "Total zones cannot be negative")
    private Integer totalZones;

    private String status = "ACTIVE";

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(length = 1000)
    private String notes;

    @OneToMany(mappedBy = "park", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<IrrigationZone> zones = new ArrayList<>();

    @OneToMany(mappedBy = "park", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MaintenanceTask> maintenanceTasks = new ArrayList<>();

    @OneToMany(mappedBy = "park", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Alert> alerts = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "ACTIVE";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getParkCode() { return parkCode; }
    public void setParkCode(String parkCode) { this.parkCode = parkCode; }

    public String getParkName() { return parkName; }
    public void setParkName(String parkName) { this.parkName = parkName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public LocalTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }

    public LocalTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }

    public Integer getTotalZones() { return totalZones; }
    public void setTotalZones(Integer totalZones) { this.totalZones = totalZones; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<IrrigationZone> getZones() { return zones; }
    public void setZones(List<IrrigationZone> zones) { this.zones = zones; }

    public List<MaintenanceTask> getMaintenanceTasks() { return maintenanceTasks; }
    public void setMaintenanceTasks(List<MaintenanceTask> maintenanceTasks) { this.maintenanceTasks = maintenanceTasks; }

    public List<Alert> getAlerts() { return alerts; }
    public void setAlerts(List<Alert> alerts) { this.alerts = alerts; }
}
