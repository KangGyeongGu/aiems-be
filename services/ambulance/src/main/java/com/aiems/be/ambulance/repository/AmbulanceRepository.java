package com.aiems.be.ambulance.repository;

import com.aiems.be.ambulance.domain.Ambulance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmbulanceRepository extends JpaRepository<Ambulance, Long> {
}
