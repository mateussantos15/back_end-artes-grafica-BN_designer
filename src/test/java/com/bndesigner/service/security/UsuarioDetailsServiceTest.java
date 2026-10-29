package com.bndesigner.service.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.bndesigner.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.domain.security.UsuarioDetails;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveCarregarUsuarioPeloEmail() {

        Usuario usuario = Usuario.builder()
                .idUsuario(1L)
                .nome("Mateus")
                .email("mateus@email.com")
                .senha("senha-codificada")
                .build();

        when(usuarioRepository.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));

        UserDetails resultado =
                usuarioDetailsService.loadUserByUsername(usuario.getEmail());

        assertInstanceOf(UsuarioDetails.class, resultado);
        assertEquals(usuario.getEmail(), resultado.getUsername());
        assertEquals(usuario.getSenha(), resultado.getPassword());

        verify(usuarioRepository).findByEmail(usuario.getEmail());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontrado() {

        String email = "naoexiste@email.com";

        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> usuarioDetailsService.loadUserByUsername(email)
        );

        verify(usuarioRepository).findByEmail(email);
    }
}