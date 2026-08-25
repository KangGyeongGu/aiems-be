package com.aiems.be.transfer.exception;

import com.aiems.be.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TransferErrorCode implements ErrorCode {
    PATIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 구급차의 환자가 존재하지 않습니다."),
    TRANSFER_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "조회된 이송 기록이 없습니다."),
    ONGOING_TRANSFER_NOT_FOUND(HttpStatus.NOT_FOUND, "진행 중인 이송 기록이 없습니다."),
    ANALYSIS_TIMEOUT(HttpStatus.SERVICE_UNAVAILABLE, "환자 분석 응답이 시간 내에 도착하지 않았습니다."),
    TREATMENT_RECORD_NOT_READY(HttpStatus.NOT_FOUND, "처치 기록이 아직 준비되지 않았습니다."),
    TREATMENT_RECORD_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "처치 기록을 해석하지 못했습니다.");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    @Override
    public String getCode() {
        return this.name();
    }
}
