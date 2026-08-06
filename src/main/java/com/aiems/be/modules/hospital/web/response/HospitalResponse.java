package com.aiems.be.modules.hospital.web.response;

import com.aiems.be.common.web.response.LocationResponse;
import com.aiems.be.modules.hospital.domain.Hospital;
import lombok.Builder;

@Builder
public record HospitalResponse(
        Long id,
        String name,
        LocationResponse location
) {

    public static HospitalResponse from(Hospital hospital) {
        return HospitalResponse.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .location(LocationResponse.from(hospital.getLocation()))
                .build();
    }

}