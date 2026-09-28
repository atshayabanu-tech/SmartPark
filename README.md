# SmartPark — Smart Public Park Maintenance & Automated Irrigation Grid

## Description
A Spring Boot MVC web app for municipal park administrators, supervisors and gardeners to manage parks,
irrigation zones, watering schedules, maintenance tasks, sensor readings and alerts from one dashboard.

## Problem Statement
Public parks often depend on manual monitoring for watering, maintenance and environmental conditions,
causing unnecessary water usage, delayed maintenance and difficulty finding areas that need attention.

## Solution
A centralized platform that monitors irrigation zones, soil moisture, environmental readings,
maintenance activity and alerts. Irrigation logic is transparent and rule-based (not AI):
"Automated irrigation recommendation — field verification recommended."

## Features
- Dashboard with live statistics (all computed from the database)
- Park, Irrigation Zone, Irrigation Schedule and Maintenance CRUD with search and filters
- Rule-based irrigation: moisture < threshold => NEEDS_WATER; heavy rain => automatic watering skipped
- Start / Complete / Cancel irrigation (updates valve status, last watered, zone state)
- Sensor readings (manual demo entry) that auto-create LOW_SOIL_MOISTURE, HIGH_TEMPERATURE,
  HEAVY_RAINFALL and IRRIGATION_REQUIRED alerts without duplicating open alerts
- Automatic OVERDUE detection (scheduled date before today and not completed)
- Alert management with resolve action and active/resolved filtering
- Bean validation, friendly error pages, responsive Bootstrap 5 UI

## Technology Stack
Java 17, Spring Boot 3.3, Spring MVC, Spring Data JPA, Thymeleaf, H2 (file-based), Jakarta Validation,
Bootstrap 5 + Bootstrap Icons (CDN), Maven.

## Project Structure
```
com.park
 ├── controller   (Dashboard, Park, Zone, Irrigation, Maintenance, Sensor, Alert)
 ├── model        (Park, IrrigationZone, IrrigationSchedule, MaintenanceTask, SensorReading, Alert)
 ├── repository   (Spring Data JPA repositories)
 ├── service      (business logic, recommendations, alert generation, dashboard stats)
 ├── config       (DataInitializer, IrrigationThresholds)
 └── exception    (ResourceNotFoundException, GlobalExceptionHandler)
resources: templates/*.html, static/css/style.css, static/js/app.js, application.properties
```

## Requirements
JDK 17+ and Maven 3.8+ (internet access on first run to download dependencies and CDN assets).

## How to Run
```
mvn spring-boot:run
```
Open http://localhost:8080

## Database
H2 file database stored in `./data/parkdb` (created automatically, persists between restarts).
Schema is created/updated automatically and demo data is seeded once (only if no parks exist).
- H2 console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/parkdb`
- Username: `sa`   Password: (blank)

To reset the demo data, stop the app and delete the `data/` folder.

## Configurable Thresholds (application.properties)
`park.threshold.rainfall` (mm, default 10), `park.threshold.temperature` (°C, default 35),
`park.threshold.default-moisture` (%, default 40).

## Demo Flow
1. Open the dashboard. 2. Parks → New Park (PK-006 Sunrise Public Park). 3. Add zone Z-011 (moisture 25, threshold 40).
4. Sensors → add reading (moisture 20, temp 38, rainfall 0) → alerts appear and an automatic schedule is recommended.
5. Irrigation → Start (valve OPEN, RUNNING) → Complete (valve CLOSED, last watered updated).
6. Maintenance → new task "Repair Park Bench" (BENCH_REPAIR, HIGH). 7. Alerts → Resolve; active count drops.

## Screenshots
_Add screenshots here._

## Future Improvements
Real IoT soil sensors / ESP32, automated water valves, weather API, GPS/GIS mapping, mobile app,
SMS/WhatsApp notifications, ML irrigation prediction, cloud deployment, water consumption analytics,
solar-powered irrigation, multi-city deployment.
