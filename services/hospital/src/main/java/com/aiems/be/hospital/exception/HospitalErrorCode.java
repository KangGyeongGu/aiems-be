package com.aiems.be.hospital.exception;

import com.aiems.be.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HospitalErrorCode implements ErrorCode {
    TREATMENT_RECORD_NOT_READY(HttpStatus.NOT_FOUND, "이송 일지가 아직 생성되지 않았습니다."),
    TREATMENT_RECORD_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이송 일지를 해석할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    @Override
    public String getCode() {
        return this.name();
    }
}
