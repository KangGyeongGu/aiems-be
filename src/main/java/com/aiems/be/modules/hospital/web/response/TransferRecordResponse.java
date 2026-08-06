package com.aiems.be.modules.hospital.web.response;

import com.aiems.be.modules.auth.domain.Ambulance;
import com.aiems.be.modules.auth.domain.Hospital;
import com.aiems.be.modules.transfer.domain.Patient;
import com.aiems.be.modules.transfer.repository.projection.TransferRecordDetail;

import java.time.Instant;

public record TransferRecordResponse(
        Long transferId,
        PatientDetail patient,
        AmbulanceDetail ambulance,
        HospitalDetail hospital,
        LocationDetail accidentLocation,
        Instant startedAt,
        Instant endedAt
) {
    public static TransferRecordResponse from(TransferRecordDetail detail) {
        return new TransferRecordResponse(
                detail.id(),
                PatientDetail.from(detail.patient()),
                AmbulanceDetail.from(detail.ambulance()),
                HospitalDetail.from(detail.hospital()),
                LocationDetail.from(detail.patient()),
                detail.startedAt(),
                detail.endedAt()
        );
    }

    public record PatientDetail(Long id, String name, Integer age, String gender,
                                String symptoms, String preKtas) {
        static PatientDetail from(Patient patient) {
            return new PatientDetail(patient.getId(), patient.getName(), patient.getAge(),
                    patient.getGender() != null ? patient.getGender().name() : null,
                    patient.getSymptoms(),
                    patient.getPreKtas() != null ? patient.getPreKtas().name() : null);
        }
    }

    public record AmbulanceDetail(Long id, String licensePlate, String fireStationName) {
        static AmbulanceDetail from(Ambulance ambulance) {
            return new AmbulanceDetail(ambulance.getId(), ambulance.getLicensePlate(), ambulance.getFireStationName());
        }
    }

    public record HospitalDetail(Long id, String name, String address) {
        static HospitalDetail from(Hospital hospital) {
            return new HospitalDetail(hospital.getId(), hospital.getName(), hospital.getLocation().getAddress());
        }
    }

    public record LocationDetail(Double lat, Double lon, String address) {
        static LocationDetail from(Patient patient) {
            if (patient.getLocation() == null) return null;
            var loc = patient.getLocation();
            return new LocationDetail(loc.getCoordinates().getY(), loc.getCoordinates().getX(), loc.getAddress());
        }
    }
}
