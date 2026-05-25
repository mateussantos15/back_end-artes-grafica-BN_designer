package com.bndesigner.service.categoria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import com.bndesigner.domain.entity.categoria.Categoria;
import com.bndesigner.domain.validation.CategoriaValidator;
import com.bndesigner.dto.request.categoria.CategoriaCreateRequest;
import com.bndesigner.dto.request.categoria.CategoriaUpdateRequest;
import com.bndesigner.dto.response.categoria.CategoriaResponse;
import com.bndesigner.exceptions.custom.BusinessException;
import com.bndesigner.exceptions.custom.ResourceNotFoundException;
import com.bndesigner.mapper.categoria.CategoriaMapper;
import com.bndesigner.repository.categoria.CategoriaRepository;
import com.bndesigner.service.categoria.impl.CategoriaServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoriaServiceImpl")
public class CategoriaServiceTest {

    @InjectMocks
    private CategoriaServiceImpl categoriaServiceImpl;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;
    
    @Mock
    private CategoriaValidator categoriaValidator;

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private Categoria categoriaEntity;
    private CategoriaResponse categoriaResponse;

    @BeforeEach
    void setUp() {
        categoriaEntity = Categoria.builder()
                .idCategoria(1L)
                .nome("Banners")
                .build();

        categoriaResponse = new CategoriaResponse(1L, "Banners", "Para Eventos e Festas");
    }

    // ─── criar ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("Deve criar categoria com sucesso quando nome não existe")
        void deveCriarCategoriaComSucesso() {
            CategoriaCreateRequest request = new CategoriaCreateRequest("Banners", "Para Eventos e Festas");
            Categoria categoriaParaSalvar = Categoria.builder().nome("Banners").build();

            // Configura os mocks para o fluxo feliz do método criar
            when(categoriaMapper.toEntity(request)).thenReturn(categoriaParaSalvar);
            when(categoriaRepository.save(categoriaParaSalvar)).thenReturn(categoriaEntity);
            when(categoriaMapper.toResponse(categoriaEntity)).thenReturn(categoriaResponse);

            CategoriaResponse resultado = categoriaServiceImpl.criar(request);

            assertThat(resultado.idCategoria()).isEqualTo(1L);
            assertThat(resultado.nome()).isEqualTo("Banners");
            
            // Verifica se o validador recebeu os parâmetros corretos (nome, null)
            verify(categoriaValidator).validarNomeCategoria("Banners", null);
            verify(categoriaRepository).save(categoriaParaSalvar);
            verify(categoriaMapper).toResponse(categoriaEntity);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando o validador acusar nome duplicado")
        void naoDeveCriarCategoriaDuplicada() {
            CategoriaCreateRequest request = new CategoriaCreateRequest("Banners", "Desc");

            // Força o validador (com método void) a lançar uma BusinessException
            doThrow(new BusinessException(HttpStatus.CONFLICT, 
            		"Conflito de dados: Nome duplicado!",
            		String.format("Já existe uma Categoria com o nome: '%s'.", 
            		request.nome())))
                    .when(categoriaValidator).validarNomeCategoria("Banners", null);

            assertThatThrownBy(() -> categoriaServiceImpl.criar(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(String.format("Já existe uma Categoria com o nome: '%s'.", request.nome()));

            // Garante que o processo foi interrompido e nada foi salvo
            verify(categoriaRepository, never()).save(any());
            verify(categoriaMapper, never()).toEntity(any());
        }
    }

    // ─── buscarPorId ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar categoria quando ID existe")
        void deveBuscarCategoriaPorIdComSucesso() {
            // EntityLookup usa o findById internamente, logo mockamos o repository
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEntity));
            when(categoriaMapper.toResponse(categoriaEntity)).thenReturn(categoriaResponse);

            CategoriaResponse resultado = categoriaServiceImpl.buscarPorId(1L);

            assertThat(resultado.idCategoria()).isEqualTo(1L);
            assertThat(resultado.nome()).isEqualTo("Banners");
            verify(categoriaRepository).findById(1L);
            verify(categoriaMapper).toResponse(categoriaEntity);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException via EntityLookup quando ID não existe")
        void deveLancarErroQuandoNaoEncontrar() {
            // Simulamos o banco vazio para o ID informado
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoriaServiceImpl.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format("'%s' com identificador '%s' não foi encontrado.", "Categoria", 99L));

            verify(categoriaMapper, never()).toResponse(any());
        }
    }

    // ─── listar ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("Deve retornar página de categorias")
        void deveListarCategoriasPaginadas() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Categoria> page = new PageImpl<>(List.of(categoriaEntity), pageable, 1);

            when(categoriaRepository.findAll(pageable)).thenReturn(page);
            when(categoriaMapper.toResponse(categoriaEntity)).thenReturn(categoriaResponse);

            Page<CategoriaResponse> resultado = categoriaServiceImpl.listar(pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(1);
            assertThat(resultado.getContent().get(0).nome()).isEqualTo("Banners");
            verify(categoriaRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não há categorias")
        void deveRetornarPaginaVaziaQuandoNaoHaCategorias() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Categoria> pageVazia = new PageImpl<>(List.of(), pageable, 0);

            when(categoriaRepository.findAll(pageable)).thenReturn(pageVazia);

            Page<CategoriaResponse> resultado = categoriaServiceImpl.listar(pageable);

            assertThat(resultado.getTotalElements()).isEqualTo(0);
            assertThat(resultado.getContent()).isEmpty();
            verify(categoriaRepository).findAll(pageable);
            verify(categoriaMapper, never()).toResponse(any());
        }
    }

    // ─── atualizar ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar categoria com sucesso")
        void deveAtualizarCategoriaComSucesso() {
            CategoriaUpdateRequest request = new CategoriaUpdateRequest("Faixas", "Nova desc");
            Categoria atualizada = Categoria.builder().idCategoria(1L).nome("Faixas").build();
            CategoriaResponse responseAtualizado = new CategoriaResponse(1L, "Faixas", "Nova desc");

            // Configuração dos Mocks baseados no fluxo sequencial da Service
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEntity));
            when(categoriaRepository.save(categoriaEntity)).thenReturn(atualizada);
            when(categoriaMapper.toResponse(atualizada)).thenReturn(responseAtualizado);

            CategoriaResponse resultado = categoriaServiceImpl.atualizar(1L, request);

            assertThat(resultado.nome()).isEqualTo("Faixas");
            
            // Verificações essenciais de fluxo
            verify(categoriaRepository).findById(1L);
            verify(categoriaValidator).validarNomeCategoria("Faixas", 1L); // O ID agora é passado no atualizar
            verify(categoriaMapper).updateEntityFromRequest(request, categoriaEntity);
            verify(categoriaRepository).save(categoriaEntity);
        }

        @Test
        @DisplayName("Deve lançar BusinessException se o validador rejeitar o novo nome no atualizar")
        void naoDeveAtualizarQuandoValidadorRecusarNome() {
            CategoriaUpdateRequest request = new CategoriaUpdateRequest("Faixas", "Desc");

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEntity));
            
            // Configura o validador para falhar durante o fluxo de atualização
            doThrow(new BusinessException(HttpStatus.CONFLICT, 
            		"Conflito de dados: Nome duplicado!",
            		String.format("Já existe uma Categoria com o nome: '%s'.", 
            		request.nome())))
                    .when(categoriaValidator).validarNomeCategoria("Faixas", 1L);

            assertThatThrownBy(() -> categoriaServiceImpl.atualizar(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(String.format("Já existe uma Categoria com o nome: '%s'.", request.nome()));

            // Garante que o mapper e o save nunca foram chamados após o erro de validação
            verify(categoriaMapper, never()).updateEntityFromRequest(any(), any());
            verify(categoriaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando ID não existe no atualizar")
        void naoDeveAtualizarQuandoIdNaoEncontrado() {
            CategoriaUpdateRequest request = new CategoriaUpdateRequest("Faixas", "Desc");

            // EntityLookup tentará buscar e falhará aqui
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoriaServiceImpl.atualizar(99L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format("'%s' com identificador '%s' não foi encontrado.", "Categoria", 99L));

            // Garante que o validador e o save foram completamente ignorados
            verify(categoriaValidator, never()).validarNomeCategoria(any(), any());
            verify(categoriaRepository, never()).save(any());
        }
    }

    // ─── deletar ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deletar()")
    class Deletar {

        @Test
        @DisplayName("Deve deletar categoria com sucesso quando ID existe")
        void deveDeletarCategoriaComSucesso() {
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEntity));

            categoriaServiceImpl.deletar(1L);

            verify(categoriaRepository).findById(1L);
            verify(categoriaRepository).delete(categoriaEntity);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando ID não existe no deletar")
        void naoDeveDeletarQuandoIdNaoEncontrado() {
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoriaServiceImpl.deletar(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format("'%s' com identificador '%s' não foi encontrado.", "Categoria", 99L));

            verify(categoriaRepository, never()).delete(any());
        }
    }
}