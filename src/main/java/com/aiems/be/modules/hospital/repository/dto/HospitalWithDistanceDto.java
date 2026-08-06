package com.aiems.be.modules.hospital.repository.dto;

import com.aiems.be.modules.hospital.domain.Hospital;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HospitalWithDistanceDto implements Comparable<HospitalWithDistanceDto> {

    private Hospital hospital;
    private Double distance;

    @Override
    public int compareTo(HospitalWithDistanceDto o) {
        return Double.compare(distance, o.distance);
    }

    public String toString() {
        return "HospitalWithDistanceDto{" +
                "hospital=" + hospital.getName() +
                ", distance=" + distance +
                '}';
    }
}
