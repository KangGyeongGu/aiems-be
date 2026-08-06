package com.aiems.be.modules.hospital.repository;

import com.aiems.be.modules.hospital.domain.HospitalSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalSpecialtyRepository extends JpaRepository<HospitalSpecialty, Long> {

    List<HospitalSpecialty> findAllByHospitalIdIn(List<Long> hospitalIds);
}
