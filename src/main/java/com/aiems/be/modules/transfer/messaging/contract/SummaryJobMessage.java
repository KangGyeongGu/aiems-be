package com.aiems.be.modules.transfer.messaging.contract;

import com.aiems.be.modules.auth.domain.Ambulance;
import com.aiems.be.modules.transfer.domain.Patient;
import com.aiems.be.modules.transfer.domain.VitalSign;

public record SummaryJobMessage(
        Long ambulanceId,
        String message,
        PatientSnapshot patient
) {
    public static SummaryJobMessage request(Long ambulanceId, String message, Patient patient) {
        return new SummaryJobMessage(ambulanceId, message, PatientSnapshot.from(patient));
    }

    public record PatientSnapshot(
            Long id,
            AmbulanceSnapshot ambulance,
            String name,
            Integer age,
            String gender,
            VitalSignSnapshot vitalSign,
            String symptoms,
            String preKtas,
            String firstAid,
            String cause,
            String underlyingDisease
    ) {
        static PatientSnapshot from(Patient patient) {
            return new PatientSnapshot(
                    patient.getId(),
                    AmbulanceSnapshot.from(patient.getAmbulance()),
                    patient.getName(),
                    patient.getAge(),
                    patient.getGender() != null ? patient.getGender().name() : null,
                    VitalSignSnapshot.from(patient.getVitalSign()),
                    patient.getSymptoms(),
                    patient.getPreKtas() != null ? patient.getPreKtas().name() : null,
                    patient.getFirstAid(),
                    patient.getCause(),
                    patient.getUnderlyingDisease()
            );
        }
    }

    public record AmbulanceSnapshot(
            Long id, String deviceId, String licensePlate,
            String fireStationName, String jurisdiction, String operationStatus
    ) {
        static AmbulanceSnapshot from(Ambulance ambulance) {
            if (ambulance == null) return null;
            return new AmbulanceSnapshot(
                    ambulance.getId(), ambulance.getDeviceId(), ambulance.getLicensePlate(),
                    ambulance.getFireStationName(),
                    ambulance.getJurisdiction() != null ? ambulance.getJurisdiction().name() : null,
                    ambulance.getOperationStatus() != null ? ambulance.getOperationStatus().name() : null
            );
        }
    }

    public record VitalSignSnapshot(
            Integer minBloodPressure, Integer maxBloodPressure,
            Integer pulse, Integer respiratoryRate, Double temperature
    ) {
        static VitalSignSnapshot from(VitalSign vitalSign) {
            if (vitalSign == null) return null;
            return new VitalSignSnapshot(
                    vitalSign.getMinBloodPressure(), vitalSign.getMaxBloodPressure(),
                    vitalSign.getPulse(), vitalSign.getRespiratoryRate(), vitalSign.getTemperature()
            );
        }
    }
}
