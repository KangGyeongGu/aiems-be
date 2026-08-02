package com.A.B.modules.auth.exception;

import com.A.B.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    LOGIN_ID_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    INVALID_OAUTH2_USER(HttpStatus.UNAUTHORIZED, "소셜 로그인 사용자 정보가 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    @Override
    public String getCode() {
        return this.name();
    }
}
