package com.aiems.be.hospital.repository.projection;

import com.aiems.be.hospital.domain.Hospital;

public record HospitalWithDistance(
        Hospital hospital,
        Double distance
) {
}
