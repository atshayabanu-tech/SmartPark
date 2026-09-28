package com.park.repository;

import com.park.model.Park;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkRepository extends JpaRepository<Park, Long> {

    Optional<Park> findByParkCode(String parkCode);

    boolean existsByParkCode(String parkCode);

    List<Park> findByStatus(String status);

    List<Park> findByLocationContainingIgnoreCase(String location);

    List<Park> findByParkNameContainingIgnoreCaseOrParkCodeContainingIgnoreCaseOrLocationContainingIgnoreCaseOrManagerNameContainingIgnoreCase(
            String parkName, String parkCode, String location, String managerName);
}
