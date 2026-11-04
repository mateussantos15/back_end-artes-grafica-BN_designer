package com.bndesigner.service.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import com.bndesigner.dto.request.login.LoginRequest;
import com.bndesigner.dto.response.login.LoginResponse;
import com.bndesigner.service.security.impl.AuthenticationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthenticationService authenticationService;

    @BeforeEach
    void configurar() {
        authenticationService =
                new AuthenticationServiceImpl(authenticationManager);
    }

    @Test
    void deveAutenticarUsuarioComCredenciaisValidas() {

        LoginRequest request =
                new LoginRequest(
                        "cliente@email.com",
                        "senha123"
                );

        LoginResponse response =
                authenticationService.autenticar(request);

        assertEquals(
                "Login realizado com sucesso",
                response.mensagem()
        );

        verify(authenticationManager).authenticate(
                org.mockito.ArgumentMatchers.any()
        );
    }
    
    @Test
    void deveFalharQuandoCredenciaisForemInvalidas() {

        LoginRequest request =
                new LoginRequest(
                        "cliente@email.com",
                        "senhaerrada"
                );

        org.mockito.Mockito.when(
                authenticationManager.authenticate(
                        org.mockito.ArgumentMatchers.any()
                )
        ).thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.autenticar(request)
        );
    }
}