package com.aiems.be.modules.ambulance.repository;

import com.aiems.be.modules.ambulance.domain.Ambulance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmbulanceRepository extends JpaRepository<Ambulance, Long> {
}
