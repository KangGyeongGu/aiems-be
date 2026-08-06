package com.aiems.be.modules.patient.repository;

import com.aiems.be.modules.patient.domain.Patient;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @EntityGraph(attributePaths = "ambulance")
    Optional<Patient> findTop1ByAmbulanceIdOrderByCreatedAtDesc(Long ambulanceId);

    @Transactional(readOnly = true)
    default Patient searchMyCurrentPatient(Long ambulanceId) {
        return findTop1ByAmbulanceIdOrderByCreatedAtDesc(ambulanceId)
                .orElseThrow(() -> new EntityNotFoundException("해당 앰뷸런스의 환자가 존재하지 않습니다. [ambulanceId = %s]".formatted(ambulanceId)));
    }

    @Transactional(readOnly = true)
    default Patient findMyCurrentPatientOrNull(Long ambulanceId) {
        return findTop1ByAmbulanceIdOrderByCreatedAtDesc(ambulanceId)
                .orElse(null);
    }
}
