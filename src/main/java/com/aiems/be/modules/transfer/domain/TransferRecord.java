package com.aiems.be.modules.transfer.domain;

import com.aiems.be.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Objects;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TransferRecord extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_record_id", updatable = false)
    private Long id;

    private Long ambulanceId;

    private Long hospitalId;

    private Long patientId;

    private String transferReport;

    @Column(columnDefinition = "json")
    private String treatmentRecord;

    private Instant startedAt;

    private Instant endedAt;

    public void markCompleteTime(Instant completedAt) {
        this.endedAt = Objects.requireNonNullElseGet(completedAt, Instant::now);
    }

    public void updateTreatmentRecord(String treatmentRecord) {
        this.treatmentRecord = treatmentRecord;
    }

    public void updateTransferReport(String transferReport) {
        this.transferReport = transferReport;
    }
}
