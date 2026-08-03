package com.aiems.be.modules.Ambulance.domain;

import com.aiems.be.common.domain.Role;
import com.aiems.be.common.domain.Sido;
import com.aiems.be.modules.auth.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Ambulance extends Member {

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

    @Override
    public String getRole() {
        return Role.AMBULANCE.toString();
    }

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

