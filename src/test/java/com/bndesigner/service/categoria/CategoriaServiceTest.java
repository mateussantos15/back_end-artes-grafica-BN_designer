package com.bndesigner.service.categoria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import com.bndesigner.domain.entity.categoria.Categoria;
import com.bndesigner.dto.request.categoria.CategoriaCreateRequest;
import com.bndesigner.dto.request.categoria.CategoriaUpdateRequest;
import com.bndesigner.dto.response.categoria.CategoriaResponse;
import com.bndesigner.exceptions.BusinessException;
import com.bndesigner.exceptions.ResourceNotFoundException;
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

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private Categoria categoriaEntity;
    private CategoriaResponse categoriaResponse;

    @BeforeEach
    void setUp() {
        categoriaEntity = Categoria.builder()
                .id(1L)
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

            CategoriaCreateRequest request =
                    new CategoriaCreateRequest("Banners", "Para Eventos e Festas");

            Categoria categoriaParaSalvar = Categoria.builder().nome("Banners").build();

            when(categoriaRepository.existsByNome("Banners")).thenReturn(false);
            when(categoriaMapper.toEntity(request)).thenReturn(categoriaParaSalvar);
            when(categoriaRepository.save(categoriaParaSalvar)).thenReturn(categoriaEntity);
            when(categoriaMapper.toResponse(categoriaEntity)).thenReturn(categoriaResponse);

            CategoriaResponse resultado = categoriaServiceImpl.criar(request);

            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.nome()).isEqualTo("Banners");
            verify(categoriaRepository).save(categoriaParaSalvar);
            verify(categoriaMapper).toResponse(categoriaEntity);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando nome já existe")
        void naoDeveCriarCategoriaDuplicada() {

            CategoriaCreateRequest request =
                    new CategoriaCreateRequest("Banners", "Desc");

            when(categoriaRepository.existsByNome("Banners")).thenReturn(true);

            assertThatThrownBy(() -> categoriaServiceImpl.criar(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Já existe uma categoria com o nome: Banners");

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

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEntity));
            when(categoriaMapper.toResponse(categoriaEntity)).thenReturn(categoriaResponse);

            CategoriaResponse resultado = categoriaServiceImpl.buscarPorId(1L);

            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.nome()).isEqualTo("Banners");
            verify(categoriaRepository).findById(1L);
            verify(categoriaMapper).toResponse(categoriaEntity);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando ID não existe")
        void deveLancarErroQuandoNaoEncontrar() {

            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoriaServiceImpl.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Nenhuma categoria encontrada com o id: " + 99L);

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
        @DisplayName("Deve atualizar categoria com sucesso quando nome não muda")
        void deveAtualizarCategoriaComMesmoNomeComSucesso() {

            CategoriaUpdateRequest request = new CategoriaUpdateRequest("Banners", "Nova desc");

            Categoria atualizada = Categoria.builder().id(1L).nome("Banners").build();
            CategoriaResponse responseAtualizado = new CategoriaResponse(1L, "Banners", "Nova desc");

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEntity));
            when(categoriaRepository.save(categoriaEntity)).thenReturn(atualizada);
            when(categoriaMapper.toResponse(atualizada)).thenReturn(responseAtualizado);

            CategoriaResponse resultado = categoriaServiceImpl.atualizar(1L, request);

            assertThat(resultado.nome()).isEqualTo("Banners");
            verify(categoriaRepository, never()).existsByNome(any());
            verify(categoriaMapper).updateEntityFromRequest(request, categoriaEntity);
            verify(categoriaRepository).save(categoriaEntity);
        }

        @Test
        @DisplayName("Deve atualizar categoria com sucesso quando novo nome não existe")
        void deveAtualizarCategoriaComNovoNomeComSucesso() {

            CategoriaUpdateRequest request = new CategoriaUpdateRequest("Faixas", "Nova desc");

            Categoria atualizada = Categoria.builder().id(1L).nome("Faixas").build();
            CategoriaResponse responseAtualizado = new CategoriaResponse(1L, "Faixas", "Nova desc");

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEntity));
            when(categoriaRepository.existsByNome("Faixas")).thenReturn(false);
            when(categoriaRepository.save(categoriaEntity)).thenReturn(atualizada);
            when(categoriaMapper.toResponse(atualizada)).thenReturn(responseAtualizado);

            CategoriaResponse resultado = categoriaServiceImpl.atualizar(1L, request);

            assertThat(resultado.nome()).isEqualTo("Faixas");
            verify(categoriaRepository).existsByNome("Faixas");
            verify(categoriaMapper).updateEntityFromRequest(request, categoriaEntity);
            verify(categoriaRepository).save(categoriaEntity);
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando novo nome já pertence a outra categoria")
        void naoDeveAtualizarQuandoNovoNomeJaExiste() {

            CategoriaUpdateRequest request = new CategoriaUpdateRequest("Faixas", "Desc");

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaEntity));
            when(categoriaRepository.existsByNome("Faixas")).thenReturn(true);

            assertThatThrownBy(() -> categoriaServiceImpl.atualizar(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Já existe uma categoria com o nome: Faixas");

            verify(categoriaRepository, never()).save(any());
            verify(categoriaMapper, never()).updateEntityFromRequest(any(), any());
        }

        @Test
        @DisplayName("Deve lançar BusinessException quando ID não existe")
        void naoDeveAtualizarQuandoIdNaoEncontrado() {

            CategoriaUpdateRequest request = new CategoriaUpdateRequest("Faixas", "Desc");

            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoriaServiceImpl.atualizar(99L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Nenhuma categoria encontrada com o id: " + 99L);

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
        @DisplayName("Deve lançar BusinessException quando ID não existe")
        void naoDeveDeletarQuandoIdNaoEncontrado() {

            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoriaServiceImpl.deletar(99L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Nenhuma categoria encontrada com o id: " + 99);

            verify(categoriaRepository, never()).delete(any());
        }
    }
}