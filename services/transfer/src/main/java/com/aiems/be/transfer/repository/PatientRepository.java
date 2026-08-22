package com.aiems.be.transfer.repository;

import com.aiems.be.transfer.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findTop1ByAmbulanceIdOrderByCreatedAtDesc(Long ambulanceId);
}
