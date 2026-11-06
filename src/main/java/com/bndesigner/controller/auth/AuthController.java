
package com.bndesigner.controller.auth;

import com.bndesigner.dto.request.login.LoginRequest;
import com.bndesigner.dto.response.login.LoginResponse;
import com.bndesigner.service.security.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response =
                authenticationService.autenticar(request);

        return ResponseEntity.ok(response);
    }
}
