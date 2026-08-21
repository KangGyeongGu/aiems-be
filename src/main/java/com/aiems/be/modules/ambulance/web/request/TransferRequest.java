package com.aiems.be.modules.ambulance.web.request;

import com.aiems.be.common.domain.Location;
import com.aiems.be.modules.auth.domain.Ambulance;
import com.aiems.be.modules.transfer.domain.Gender;
import com.aiems.be.modules.transfer.domain.Patient;
import com.aiems.be.modules.transfer.domain.PreKTAS;
import com.aiems.be.modules.transfer.domain.VitalSign;
import com.aiems.be.modules.transfer.messaging.contract.PatientAnalysisRequest;

import java.util.Arrays;
import java.util.List;

public record TransferRequest(
        String name,
        Integer age,
        String gender,
        String symptoms,
        String cause,
        String firstAid,
        List<String> underlyingDisease,
        VitalRequest vitalSign,
        LocationRequest accidentLocation
) {

    public TransferRequest {
        Arrays.stream(Gender.values())
                .filter(g -> g.name().equals(gender))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[%s] 는 유효하지 않은 성별입니다.".formatted(gender)));
    }

    public PatientAnalysisRequest toAnalysisRequest() {
        return new PatientAnalysisRequest(
                symptoms(), underlyingDisease(), Gender.valueOf(gender()),
                age(), vitalSign().toVitalSign(), cause());
    }

    public Patient toEntity(PreKTAS preKtas, Ambulance ambulance) {
        return Patient.builder()
                .name(name())
                .age(age())
                .gender(Gender.valueOf(gender()))
                .symptoms(symptoms())
                .preKtas(preKtas)
                .vitalSign(vitalSign().toVitalSign())
                .ambulance(ambulance)
                .location(accidentLocation().toLocation())
                .firstAid(firstAid())
                .cause(cause())
                .underlyingDisease(String.join(", ", underlyingDisease()))
                .build();
    }

    public record VitalRequest(
            Integer minBloodPressure,
            Integer maxBloodPressure,
            Integer pulse,
            Integer respiratoryRate,
            Double temperature
    ) {
        VitalSign toVitalSign() {
            return VitalSign.builder()
                    .minBloodPressure(minBloodPressure())
                    .maxBloodPressure(maxBloodPressure())
                    .pulse(pulse())
                    .respiratoryRate(respiratoryRate())
                    .temperature(temperature())
                    .build();
        }
    }

    public record LocationRequest(Double lat, Double lon, String address) {
        Location toLocation() {
            return Location.of(Location.createCoordinates(lon(), lat()), address());
        }
    }
}
