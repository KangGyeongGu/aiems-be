package com.aiems.be.modules.transfer.repository.dto;

import com.aiems.be.common.domain.Location;
import com.aiems.be.modules.ambulance.domain.Ambulance;
import com.aiems.be.modules.hospital.domain.Hospital;
import com.aiems.be.modules.patient.domain.Patient;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@ToString
@Getter
@Builder
public class TransferRecordDto {

    private Long id;

    private Ambulance ambulance;

    private Hospital hospital;

    private Patient patient;

    private String transferReport;

    private String treatmentRecord;

    private Location location;

    private Instant startedAt;

    private Instant endedAt;

    private Instant createdAt;

    private Instant updatedAt;
}
