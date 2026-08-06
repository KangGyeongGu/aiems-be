package com.aiems.be.modules.hospital.repository;

import com.aiems.be.modules.hospital.domain.Hospital;
import com.aiems.be.modules.hospital.domain.HospitalSpecialty;
import com.aiems.be.modules.hospital.domain.Specialty;
import com.aiems.be.modules.hospital.repository.dto.HospitalWithSpecialtyDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface HospitalSpecialtyRepository extends JpaRepository<HospitalSpecialty, Long> {

    List<HospitalSpecialty> findAllByHospitalIdIn(List<Long> hospitalId);

    @Transactional(readOnly = true)
    default List<HospitalWithSpecialtyDto> findHospitalsWithSpecialties(List<Long> hospitalId) {
        List<HospitalSpecialty> hospitalSpecialties = findAllByHospitalIdIn(hospitalId);
        Map<Hospital, List<Specialty>> HospitalWithSpecialties = hospitalSpecialties.stream()
                .collect(Collectors.groupingBy(
                        HospitalSpecialty::getHospital,
                        Collectors.mapping(HospitalSpecialty::getSpecialty, Collectors.toList()
                        )));

        List<HospitalWithSpecialtyDto> hospitalWithSpecialtyDtos = new ArrayList<>();
        for (Map.Entry<Hospital, List<Specialty>> hospitalListEntry : HospitalWithSpecialties.entrySet()) {
            HospitalWithSpecialtyDto dto = HospitalWithSpecialtyDto.builder()
                    .hospital(hospitalListEntry.getKey())
                    .specialties(hospitalListEntry.getValue())
                    .build();

            hospitalWithSpecialtyDtos.add(dto);
        }

        return hospitalWithSpecialtyDtos;
    }
}
