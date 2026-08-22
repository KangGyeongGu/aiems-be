package com.aiems.be.transfer.messaging;

import com.aiems.be.common.domain.Location;
import com.aiems.be.common.domain.VitalSign;
import com.aiems.be.transfer.domain.Patient;
import com.aiems.be.contracts.transfer.TransferRequestMessage;
import com.aiems.be.contracts.ai.SummaryJobMessage;

import java.time.Instant;

public final class TransferMessageMapper {

    private TransferMessageMapper() {
    }

    public static TransferRequestMessage toRequestMessage(Patient patient, Double distance) {
        return new TransferRequestMessage(
                new TransferRequestMessage.AmbulanceInfo(
                        patient.getAmbulanceId(),
                        patient.getAmbulanceLicensePlate(),
                        patient.getAmbulanceFireStationName()),
                toPatientInfo(patient),
                toLocationInfo(patient.getLocation()),
                distance,
                Instant.now());
    }

    private static TransferRequestMessage.PatientInfo toPatientInfo(Patient patient) {
        return new TransferRequestMessage.PatientInfo(
                patient.getId(),
                patient.getName(),
                patient.getAge(),
                patient.getGender() != null ? patient.getGender().name() : null,
                toVitalSignInfo(patient.getVitalSign()),
                patient.getSymptoms(),
                patient.getPreKtas() != null ? patient.getPreKtas().name() : null,
                patient.getFirstAid(),
                patient.getCause(),
                patient.getUnderlyingDisease());
    }

    private static TransferRequestMessage.VitalSignInfo toVitalSignInfo(VitalSign vs) {
        if (vs == null) return null;
        return new TransferRequestMessage.VitalSignInfo(
                vs.getMinBloodPressure(), vs.getMaxBloodPressure(),
                vs.getPulse(), vs.getRespiratoryRate(), vs.getTemperature());
    }

    private static TransferRequestMessage.LocationInfo toLocationInfo(Location location) {
        if (location == null) return null;
        return new TransferRequestMessage.LocationInfo(
                location.getCoordinates().getY(),
                location.getCoordinates().getX(),
                location.getAddress());
    }

    public static SummaryJobMessage toSummaryJobMessage(Long ambulanceId, String audioKey, Patient patient) {
        return new SummaryJobMessage(ambulanceId, audioKey, toPatientSnapshot(patient));
    }

    private static SummaryJobMessage.PatientSnapshot toPatientSnapshot(Patient patient) {
        return new SummaryJobMessage.PatientSnapshot(
                patient.getId(),
                toAmbulanceSnapshot(patient),
                patient.getName(),
                patient.getAge(),
                patient.getGender() != null ? patient.getGender().name() : null,
                toVitalSignSnapshot(patient.getVitalSign()),
                patient.getSymptoms(),
                patient.getPreKtas() != null ? patient.getPreKtas().name() : null,
                patient.getFirstAid(),
                patient.getCause(),
                patient.getUnderlyingDisease());
    }

    private static SummaryJobMessage.AmbulanceSnapshot toAmbulanceSnapshot(Patient patient) {
        return new SummaryJobMessage.AmbulanceSnapshot(
                patient.getAmbulanceId(),
                patient.getAmbulanceDeviceId(),
                patient.getAmbulanceLicensePlate(),
                patient.getAmbulanceFireStationName(),
                patient.getAmbulanceJurisdiction(),
                patient.getAmbulanceOperationStatus());
    }

    private static SummaryJobMessage.VitalSignSnapshot toVitalSignSnapshot(VitalSign vs) {
        if (vs == null) return null;
        return new SummaryJobMessage.VitalSignSnapshot(
                vs.getMinBloodPressure(), vs.getMaxBloodPressure(),
                vs.getPulse(), vs.getRespiratoryRate(), vs.getTemperature());
    }
}
