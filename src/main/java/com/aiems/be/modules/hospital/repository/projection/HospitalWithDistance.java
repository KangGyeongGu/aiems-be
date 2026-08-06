package com.aiems.be.modules.hospital.repository.projection;

import com.aiems.be.modules.auth.domain.Hospital;

public record HospitalWithDistance(
        Hospital hospital,
        Double distance
) {
}
