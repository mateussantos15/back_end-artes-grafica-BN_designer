
package com.bndesigner.service.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.mock;

class AuthenticationProviderTest {

    private UserDetailsService usuarioDetailsService;
    private PasswordEncoder passwordEncoder;
    private DaoAuthenticationProvider authenticationProvider;

    @BeforeEach
    void configurar() {
        usuarioDetailsService = mock(UserDetailsService.class);
        passwordEncoder = new BCryptPasswordEncoder();

        authenticationProvider =
                new DaoAuthenticationProvider(usuarioDetailsService);

        authenticationProvider.setPasswordEncoder(passwordEncoder);
    }

    @Test
    void deveAutenticarUsuarioComCredenciaisCorretas() {
        String email = "cliente@email.com";
        String senha = "senha123";

        UserDetails usuario = User.builder()
                .username(email)
                .password(passwordEncoder.encode(senha))
                .authorities("ROLE_CLIENTE")
                .build();

        when(usuarioDetailsService.loadUserByUsername(email))
                .thenReturn(usuario);

        Authentication autenticacao =
                authenticationProvider.authenticate(
                        new UsernamePasswordAuthenticationToken(email, senha)
                );

        assertNotNull(autenticacao);
    }

    @Test
    void deveRejeitarUsuarioComSenhaIncorreta() {
        String email = "cliente@email.com";
        String senhaCorreta = "senha123";
        String senhaIncorreta = "senhaerrada";

        UserDetails usuario = User.builder()
                .username(email)
                .password(passwordEncoder.encode(senhaCorreta))
                .authorities("ROLE_CLIENTE")
                .build();

        when(usuarioDetailsService.loadUserByUsername(email))
                .thenReturn(usuario);

        assertThrows(BadCredentialsException.class, () ->
                authenticationProvider.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email, senhaIncorreta
                        )
                )
        );
    }
}
