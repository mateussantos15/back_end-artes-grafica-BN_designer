package com.bndesigner.service.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.domain.validation.UsuarioValidator;
import com.bndesigner.dto.request.usuario.UsuarioCreateRequest;
import com.bndesigner.dto.request.usuario.UsuarioUpdateRequest;
import com.bndesigner.dto.response.usuario.UsuarioResponse;
import com.bndesigner.exceptions.custom.BusinessException;
import com.bndesigner.exceptions.custom.ResourceNotFoundException;
import com.bndesigner.mapper.usuario.UsuarioMapper;
import com.bndesigner.repository.usuario.UsuarioRepository;
import com.bndesigner.service.usuario.impl.UsuarioServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioServiceImpl")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;
    
    @Mock
    private UsuarioValidator usuarioValidator; // Injetado para gerenciar as validações de email

    @InjectMocks
    private UsuarioServiceImpl implService;

    // ─────────────────────────────────────────────────────────────────────────
    // criar()
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve criar usuário com sucesso e retornar UsuarioResponse")
        void deveCriarUsuarioComSucesso() {
            // Arrange
            UsuarioCreateRequest request = new UsuarioCreateRequest("Mateus", "Santos", "mateus@email.com", "123");
            Usuario usuario = new Usuario();
            Usuario salvo = new Usuario();
            UsuarioResponse response = new UsuarioResponse(1L, "Mateus", "Santos", "mateus@email.com");

            // O validador é void, então por padrão ele não faz nada (sucesso na validação)
            when(usuarioMapper.toEntity(request)).thenReturn(usuario);
            when(usuarioRepository.save(usuario)).thenReturn(salvo);
            when(usuarioMapper.toResponse(salvo)).thenReturn(response);

            // Act
            UsuarioResponse resultado = implService.criar(request);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.nome()).isEqualTo("Mateus");
            
            // Verifica se a validação foi chamada com ID null (já que está criando)
            verify(usuarioValidator).validarEmail(null, request.email());
            verify(usuarioRepository).save(usuario);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o validador acusar email já existente")
        void deveLancarExcecaoQuandoEmailJaExistirAoCriar() {
            // Arrange
            UsuarioCreateRequest request = new UsuarioCreateRequest("Mateus", "Santos", "mateus@email.com", "123");

            // Força o validador (método void) a lançar a exceção de negócio
            doThrow(new BusinessException(HttpStatus.CONFLICT, "Email já cadastrado", "O email já está em uso"))
                .when(usuarioValidator).validarEmail(null, request.email());

            // Act & Assert
            assertThatThrownBy(() -> implService.criar(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("O email já está em uso");

            verify(usuarioRepository, never()).save(any());
            verify(usuarioMapper, never()).toEntity(any());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // buscarPorId()
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve buscar usuário por id com sucesso")
        void deveBuscarUsuarioPorIdComSucesso() {
            // Arrange
            Usuario usuario = new Usuario();
            UsuarioResponse response = new UsuarioResponse(1L, "Mateus", "", "mateus@email.com");

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(usuarioMapper.toResponse(usuario)).thenReturn(response);

            // Act
            UsuarioResponse resultado = implService.buscarPorId(1L);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.nome()).isEqualTo("Mateus");
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando usuário não existir")
        void deveLancarExcecaoQuandoUsuarioNaoExistirAoBuscar() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> implService.buscarPorId(1L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // listar()
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("deve listar usuários paginados com sucesso")
        void deveListarUsuariosComSucesso() {
            // Arrange
            Usuario usuario = new Usuario();
            UsuarioResponse response = new UsuarioResponse(1L, "Mateus", "", "mateus@email.com");
            Page<Usuario> page = new PageImpl<>(List.of(usuario));

            when(usuarioRepository.findAll(any(Pageable.class))).thenReturn(page);
            when(usuarioMapper.toResponse(usuario)).thenReturn(response);

            // Act
            Page<UsuarioResponse> resultado = implService.listar(Pageable.unpaged());

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getTotalElements()).isEqualTo(1);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // atualizar()
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar usuário com sucesso")
        void deveAtualizarUsuarioComSucesso() {
            // Arrange
            UsuarioUpdateRequest request = new UsuarioUpdateRequest("Mateus", "", "novo@email.com");
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(1L); // Definido ID para validação correta do método de update
            usuario.setEmail("antigo@email.com");

            Usuario salvo = new Usuario();
            UsuarioResponse response = new UsuarioResponse(1L, "Mateus", "", "novo@email.com");

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.save(usuario)).thenReturn(salvo);
            when(usuarioMapper.toResponse(salvo)).thenReturn(response);

            // Act
            UsuarioResponse resultado = implService.atualizar(1L, request);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.email()).isEqualTo("novo@email.com");

            // Verifica se chamou a validação passando o ID correspondente ao usuário que está sendo editado
            verify(usuarioValidator).validarEmail(1L, request.email());
            verify(usuarioMapper).updateEntityFromRequest(request, usuario);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando o validador recusar o e-mail na atualização")
        void deveLancarExcecaoQuandoEmailJaExistirAoAtualizar() {
            // Arrange
            UsuarioUpdateRequest request = new UsuarioUpdateRequest("Mateus", " ", "novo@email.com");
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(1L);
            usuario.setEmail("antigo@email.com");

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            
            // Força o validador a falhar no cenário de update
            doThrow(new BusinessException(HttpStatus.CONFLICT, "Email já cadastrado", "O email já está em uso"))
                .when(usuarioValidator).validarEmail(1L, request.email());

            // Act & Assert
            assertThatThrownBy(() -> implService.atualizar(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("O email já está em uso");

            verify(usuarioRepository, never()).save(any());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // deletar()
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deletar()")
    class Deletar {

        @Test
        @DisplayName("deve deletar usuário existente com sucesso")
        void deveDeletarUsuarioComSucesso() {
            // Arrange
            Usuario usuario = new Usuario();
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

            // Act
            implService.deletar(1L);

            // Assert
            verify(usuarioRepository).delete(usuario);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException ao tentar deletar id inexistente")
        void deveLancarExcecaoQuandoUsuarioNaoExistirAoDeletar() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> implService.deletar(1L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(usuarioRepository, never()).delete(any());
        }
    }
}