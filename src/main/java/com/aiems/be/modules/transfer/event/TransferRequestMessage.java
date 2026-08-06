package com.aiems.be.modules.transfer.event;

import com.aiems.be.modules.auth.domain.Ambulance;
import com.aiems.be.modules.transfer.domain.Patient;

import java.time.Instant;

public record TransferRequestMessage(
        AmbulanceInfo ambulance,
        PatientInfo patient,
        LocationInfo accidentLocation,
        Double distance,
        Instant requestedAt
) {
    public static TransferRequestMessage of(Patient patient, Ambulance ambulance, Double distance) {
        return new TransferRequestMessage(
                AmbulanceInfo.from(ambulance),
                PatientInfo.from(patient),
                LocationInfo.from(patient),
                distance,
                Instant.now()
        );
    }

    public record AmbulanceInfo(Long id, String licensePlate, String fireStationName) {
        static AmbulanceInfo from(Ambulance ambulance) {
            return new AmbulanceInfo(ambulance.getId(), ambulance.getLicensePlate(), ambulance.getFireStationName());
        }
    }

    public record PatientInfo(
            Long id, String name, Integer age, String gender,
            VitalSignInfo vitalSign, String symptoms, String preKtas,
            String firstAid, String cause, String underlyingDisease
    ) {
        static PatientInfo from(Patient patient) {
            return new PatientInfo(
                    patient.getId(), patient.getName(), patient.getAge(),
                    patient.getGender() != null ? patient.getGender().name() : null,
                    VitalSignInfo.from(patient),
                    patient.getSymptoms(),
                    patient.getPreKtas() != null ? patient.getPreKtas().name() : null,
                    patient.getFirstAid(), patient.getCause(), patient.getUnderlyingDisease()
            );
        }
    }

    public record VitalSignInfo(Integer minBloodPressure, Integer maxBloodPressure,
                                Integer pulse, Integer respiratoryRate, Double temperature) {
        static VitalSignInfo from(Patient patient) {
            if (patient.getVitalSign() == null) return null;
            var vs = patient.getVitalSign();
            return new VitalSignInfo(vs.getMinBloodPressure(), vs.getMaxBloodPressure(),
                    vs.getPulse(), vs.getRespiratoryRate(), vs.getTemperature());
        }
    }

    public record LocationInfo(Double lat, Double lon, String address) {
        static LocationInfo from(Patient patient) {
            if (patient.getLocation() == null) return null;
            var loc = patient.getLocation();
            return new LocationInfo(loc.getCoordinates().getY(), loc.getCoordinates().getX(), loc.getAddress());
        }
    }
}
