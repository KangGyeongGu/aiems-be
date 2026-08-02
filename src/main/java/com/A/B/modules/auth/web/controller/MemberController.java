package com.A.B.modules.auth.web.controller;

import com.A.B.common.util.SecurityUtil;
import com.A.B.modules.auth.service.AuthService;
import com.A.B.modules.auth.web.RefreshTokenCookieManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final AuthService authService;
    private final RefreshTokenCookieManager refreshTokenCookieManager;

    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw() {
        Long memberId = Long.valueOf(SecurityUtil.getCurrentUserId());
        authService.withdraw(memberId);

        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieManager.expire().toString())
                .build();
    }
}
