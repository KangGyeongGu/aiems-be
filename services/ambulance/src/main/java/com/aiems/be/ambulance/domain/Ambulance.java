package com.aiems.be.ambulance.domain;

import com.aiems.be.common.domain.BaseTimeEntity;
import com.aiems.be.common.domain.Sido;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Ambulance extends BaseTimeEntity {

    @Id
    @Column(name = "ambulance_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private String fireStationName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Sido jurisdiction;

    @Enumerated(EnumType.STRING)
    private OperationStatus operationStatus;

    public OperationStatus updateNextStatus(OperationStatus nextStatus) {
        if (operationStatus.equals(nextStatus)) {
            return this.operationStatus;
        }

        if (nextStatus != operationStatus.getNextStatus()) {
            throw new IllegalArgumentException("%s -> %s 로의 상태 변경은 허용되지 않습니다.".formatted(this.operationStatus.name(), nextStatus.name()));
        }

        operationStatus = operationStatus.getNextStatus();
        return this.operationStatus;
    }
}
