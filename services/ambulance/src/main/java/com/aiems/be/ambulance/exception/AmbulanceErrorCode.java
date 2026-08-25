package com.aiems.be.ambulance.exception;

import com.aiems.be.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AmbulanceErrorCode implements ErrorCode {
    AMBULANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 구급차입니다.");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    @Override
    public String getCode() {
        return this.name();
    }
}
