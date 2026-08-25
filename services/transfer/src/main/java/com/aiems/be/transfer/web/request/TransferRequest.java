package com.aiems.be.transfer.web.request;

import com.aiems.be.common.domain.Gender;
import com.aiems.be.common.domain.Location;
import com.aiems.be.common.domain.VitalSign;
import com.aiems.be.contracts.ai.PatientAnalysisRequest;
import com.aiems.be.contracts.ambulance.AmbulanceSnapshot;
import com.aiems.be.transfer.domain.Patient;

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

    public PatientAnalysisRequest toAnalysisRequest() {
        return new PatientAnalysisRequest(
                symptoms, underlyingDisease, Gender.valueOf(gender),
                age, vitalSign.toVitalSign(), cause);
    }

    public Patient toEntity(com.aiems.be.common.domain.PreKTAS preKtas, AmbulanceSnapshot ambulance) {
        return Patient.builder()
                .ambulanceId(ambulance.id())
                .ambulanceLicensePlate(ambulance.licensePlate())
                .ambulanceFireStationName(ambulance.fireStationName())
                .ambulanceDeviceId(ambulance.deviceId())
                .ambulanceJurisdiction(ambulance.jurisdiction())
                .ambulanceOperationStatus(ambulance.operationStatus())
                .name(name)
                .age(age)
                .gender(Gender.valueOf(gender))
                .symptoms(symptoms)
                .preKtas(preKtas)
                .vitalSign(vitalSign.toVitalSign())
                .location(accidentLocation.toLocation())
                .firstAid(firstAid)
                .cause(cause)
                .underlyingDisease(underlyingDisease != null ? String.join(", ", underlyingDisease) : null)
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
                    .minBloodPressure(minBloodPressure)
                    .maxBloodPressure(maxBloodPressure)
                    .pulse(pulse)
                    .respiratoryRate(respiratoryRate)
                    .temperature(temperature)
                    .build();
        }
    }

    public record LocationRequest(Double lat, Double lon, String address) {
        Location toLocation() {
            return Location.of(Location.createCoordinates(lon, lat), address);
        }
    }
}
