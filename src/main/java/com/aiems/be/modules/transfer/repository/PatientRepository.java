package com.aiems.be.modules.transfer.repository;

import com.aiems.be.modules.transfer.domain.Patient;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @EntityGraph(attributePaths = "ambulance")
    Optional<Patient> findTop1ByAmbulanceIdOrderByCreatedAtDesc(Long ambulanceId);
}
