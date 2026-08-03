package com.aiems.be.modules.auth.web.controller;

import com.aiems.be.modules.auth.service.AuthService;
import com.aiems.be.modules.auth.web.TokenResponseWriter;
import com.aiems.be.modules.auth.web.request.AmbulanceLoginRequest;
import com.aiems.be.modules.auth.web.request.LoginRequest;
import com.aiems.be.modules.auth.web.response.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenResponseWriter tokenResponseWriter;

    @PostMapping("/hospital")
    public ResponseEntity<TokenResponse> loginHospital(@Valid @RequestBody LoginRequest request) {
        return tokenResponseWriter.write(authService.loginHospital(request.loginId(), request.password()));
    }

    @PostMapping("/control")
    public ResponseEntity<TokenResponse> loginControl(@Valid @RequestBody LoginRequest request) {
        return tokenResponseWriter.write(authService.loginControl(request.loginId(), request.password()));
    }

    @PostMapping("/ambulance")
    public ResponseEntity<TokenResponse> loginAmbulance(@Valid @RequestBody AmbulanceLoginRequest request) {
        return tokenResponseWriter.write(authService.loginAmbulance(request.loginId(), request.password(), request.deviceId()));
    }
}
