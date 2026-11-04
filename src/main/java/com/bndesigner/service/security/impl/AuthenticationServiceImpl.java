package com.bndesigner.service.security.impl;

import com.bndesigner.dto.request.login.LoginRequest;
import com.bndesigner.dto.response.login.LoginResponse;
import com.bndesigner.service.security.AuthenticationService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse autenticar(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.senha()
                )
        );

        return new LoginResponse("Login realizado com sucesso");
    }
}