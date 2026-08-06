package com.aiems.be.modules.hospital.repository.dto;

import com.aiems.be.modules.hospital.domain.Hospital;
import com.aiems.be.modules.hospital.domain.Specialty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Builder
@EqualsAndHashCode
public class HospitalWithSpecialtyDto {
    private Hospital hospital;
    private List<Specialty> specialties;
}
