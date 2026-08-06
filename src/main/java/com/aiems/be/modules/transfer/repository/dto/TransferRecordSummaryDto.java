package com.aiems.be.modules.transfer.repository.dto;

import com.aiems.be.modules.patient.domain.Gender;
import com.aiems.be.modules.patient.domain.PreKTAS;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@ToString
@Getter
@Builder
public class TransferRecordSummaryDto {
    private Long id;
    private Instant endedAt;

    private String ambulanceLicensePlate;
    private String ambulanceFireStationName;

    private Long patientId;
    private String patientName;
    private Integer patientAge;
    private Gender patientGender;
    private String patientSymptoms;
    private PreKTAS preKTAS;
}
