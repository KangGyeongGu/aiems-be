package com.aiems.be.modules.auth.web.controller;

import com.aiems.be.modules.auth.domain.Member;
import com.aiems.be.modules.auth.service.AuthService;
import com.aiems.be.modules.auth.web.TokenResponseWriter;
import com.aiems.be.modules.auth.web.request.LoginRequest;
import com.aiems.be.modules.auth.web.request.SignupRequest;
import com.aiems.be.modules.auth.web.response.MemberResponse;
import com.aiems.be.modules.auth.web.response.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth.local.enabled", havingValue = "true")
public class LocalAuthController {

    private final AuthService authService;
    private final TokenResponseWriter tokenResponseWriter;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse signup(@Valid @RequestBody SignupRequest request) {
        Member member = authService.signup(request.loginId(), request.password(), request.email(), request.username());
        return MemberResponse.from(member);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return tokenResponseWriter.write(authService.login(request.loginId(), request.password()));
    }

}
