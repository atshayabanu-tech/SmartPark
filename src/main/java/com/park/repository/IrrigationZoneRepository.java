package com.park.repository;

import com.park.model.IrrigationZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IrrigationZoneRepository extends JpaRepository<IrrigationZone, Long> {

    Optional<IrrigationZone> findByZoneCode(String zoneCode);

    boolean existsByZoneCode(String zoneCode);

    List<IrrigationZone> findByParkId(Long parkId);

    List<IrrigationZone> findByIrrigationStatus(String irrigationStatus);

    List<IrrigationZone> findByZoneNameContainingIgnoreCase(String zoneName);

    long countByIrrigationStatus(String irrigationStatus);

    List<IrrigationZone> findByZoneNameContainingIgnoreCaseOrZoneCodeContainingIgnoreCaseOrPlantTypeContainingIgnoreCaseOrSoilTypeContainingIgnoreCase(
            String zoneName, String zoneCode, String plantType, String soilType);
}
