package com.bndesigner.service.usuario;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.service.exception.usuario.EmailJaCadastradoException;
import com.bndesigner.service.exception.usuario.UsuarioNaoEncontradoException;
import com.bndesigner.dto.request.usuario.UsuarioCreateRequest;
import com.bndesigner.dto.request.usuario.UsuarioUpdateRequest;
import com.bndesigner.dto.response.usuario.UsuarioResponse;
import com.bndesigner.mapper.usuario.UsuarioMapper;
import com.bndesigner.repository.usuario.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCriarUsuarioComSucesso() {
        UsuarioCreateRequest request =
                new UsuarioCreateRequest("Mateus", "Santos", "mateus@email.com", "123");

        Usuario usuario = new Usuario();
        Usuario salvo = new Usuario();
        UsuarioResponse response =
                new UsuarioResponse(1L, "Mateus", "Santos", "mateus@email.com");

        when(usuarioRepository.existsByEmail(request.emai()))
                .thenReturn(false);
        when(usuarioMapper.toEntity(request))
                .thenReturn(usuario);
        when(usuarioRepository.save(usuario))
                .thenReturn(salvo);
        when(usuarioMapper.toResponse(salvo))
                .thenReturn(response);

        UsuarioResponse resultado = usuarioService.criar(request);

        assertNotNull(resultado);
        assertEquals("Mateus", resultado.nome());

        verify(usuarioRepository).existsByEmail(request.emai());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExistirAoCriar() {
        UsuarioCreateRequest request =
                new UsuarioCreateRequest("Mateus", "Santos", "mateus@email.com", "123");

        when(usuarioRepository.existsByEmail(request.emai()))
                .thenReturn(true);

        assertThrows(EmailJaCadastradoException.class,
                () -> usuarioService.criar(request));

        verify(usuarioRepository, never()).save(any());
        verify(usuarioMapper, never()).toEntity(any());
    }

    @Test
    void deveBuscarUsuarioPorIdComSucesso() {
        Usuario usuario = new Usuario();
        UsuarioResponse response =
                new UsuarioResponse(1L, "Mateus", "", "mateus@email.com");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponse(usuario))
                .thenReturn(response);

        UsuarioResponse resultado = usuarioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Mateus", resultado.nome());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistirAoBuscar() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class,
                () -> usuarioService.buscarPorId(1L));
    }

    @Test
    void deveListarUsuariosComSucesso() {
        Usuario usuario = new Usuario();
        UsuarioResponse response =
                new UsuarioResponse(1L, "Mateus", "", "mateus@email.com");

        Page<Usuario> page = new PageImpl<>(List.of(usuario));

        when(usuarioRepository.findAll(any(Pageable.class)))
                .thenReturn(page);
        when(usuarioMapper.toResponse(usuario))
                .thenReturn(response);

        Page<UsuarioResponse> resultado =
                usuarioService.listarTodos(Pageable.unpaged());

        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        UsuarioUpdateRequest request =
                new UsuarioUpdateRequest("Mateus", "", "novo@email.com");

        Usuario usuario = new Usuario();
        usuario.setEmail("antigo@email.com");

        Usuario salvo = new Usuario();
        UsuarioResponse response =
                new UsuarioResponse(1L, "Mateus", "", "novo@email.com");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail(request.email()))
                .thenReturn(false);
        when(usuarioRepository.save(usuario))
                .thenReturn(salvo);
        when(usuarioMapper.toResponse(salvo))
                .thenReturn(response);

        UsuarioResponse resultado =
                usuarioService.atualizar(1L, request);

        assertEquals("novo@email.com", resultado.email());

        verify(usuarioMapper)
                .updateEntityFromRequest(request, usuario);
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaExistirAoAtualizar() {
        UsuarioUpdateRequest request =
                new UsuarioUpdateRequest("Mateus", " ", "novo@email.com");

        Usuario usuario = new Usuario();
        usuario.setEmail("antigo@email.com");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail(request.email()))
                .thenReturn(true);

        assertThrows(EmailJaCadastradoException.class,
                () -> usuarioService.atualizar(1L, request));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveRemoverUsuarioComSucesso() {
        Usuario usuario = new Usuario();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        usuarioService.remover(1L);

        verify(usuarioRepository).delete(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistirAoRemover() {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class,
                () -> usuarioService.remover(1L));

        verify(usuarioRepository, never()).delete(any());
    }
}