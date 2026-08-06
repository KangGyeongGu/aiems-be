package com.aiems.be.modules.transfer.web.request;

import com.aiems.be.modules.patient.domain.Gender;
import com.aiems.be.modules.patient.domain.PreKTAS;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Getter
@Builder
public class TransferRecordSearchRequest {

    private String patientName;
    private Integer patientAge;
    private Gender patientGender;
    private String symptoms;
    private PreKTAS preKTAS;
    private String ambulanceLicensePlate;
    private String fireStationName;
    private String accidentAddress;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant startedAtFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant startedAtTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant endedAtFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant endedAtTo;
}
