package com.bndesigner.service.produto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import com.bndesigner.domain.entity.arquivo.Arquivo;
import com.bndesigner.domain.entity.categoria.Categoria;
import com.bndesigner.domain.entity.produto.Produto;
import com.bndesigner.dto.request.produto.ProdutoCreateRequest;
import com.bndesigner.dto.request.produto.ProdutoUpdateRequest;
import com.bndesigner.dto.response.produto.ProdutoResponse;
import com.bndesigner.exceptions.BusinessException;
import com.bndesigner.exceptions.ResourceNotFoundException;
import com.bndesigner.mapper.produto.ProdutoMapper;
import com.bndesigner.repository.arquivo.ArquivoRepository;
import com.bndesigner.repository.categoria.CategoriaRepository;
import com.bndesigner.repository.produto.ProdutoRepository;
import com.bndesigner.service.produto.impl.ProdutoServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProdutoServiceImpl")
class ProdutoServiceImplTest {

    // ─────────────────────────────────────────────────────────────────────────
    // Mocks e SUT
    // ─────────────────────────────────────────────────────────────────────────

    @Mock
    private ProdutoRepository repository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ArquivoRepository arquivoRepository;

    @Mock
    private ProdutoMapper mapper;

    @InjectMocks
    private ProdutoServiceImpl service;

    // ─────────────────────────────────────────────────────────────────────────
    // Fixtures
    // ─────────────────────────────────────────────────────────────────────────

    private Categoria categoriaFake() {
        Categoria c = new Categoria();
        c.setIdCategoria(1L);
        c.setNome("Design");
        return c;
    }

    private Arquivo arquivoAtivoFake() {
        Arquivo a = new Arquivo();
        a.setIdArquivo(10L);
        a.setAtivo(true);
        return a;
    }

    private Arquivo arquivoInativoFake() {
        Arquivo a = new Arquivo();
        a.setIdArquivo(99L);
        a.setAtivo(false);
        return a;
    }

    private Produto produtoFake() {
        Produto p = new Produto();
        p.setIdProduto(100L);
        p.setTitulo("Logo Premium");
        p.setDescricao("Logotipo profissional");
        p.setPreco(new BigDecimal("49.90"));
        p.setCategoria(categoriaFake());
        return p;
    }

    private ProdutoResponse responseFake() {
        return new ProdutoResponse(
                100L,
                "Logo Premium",
                "Logotipo profissional",
                new BigDecimal("49.90"),
                LocalDateTime.now(),
                1L,
                "Design",
                10L
        );
    }

    private ProdutoCreateRequest createRequestComArquivo() {
        return new ProdutoCreateRequest("Logo Premium", "Logotipo profissional",
                new BigDecimal("49.90"), 1L, 10L);
    }

    private ProdutoCreateRequest createRequestSemArquivo() {
        return new ProdutoCreateRequest("Logo Premium", "Logotipo profissional",
                new BigDecimal("49.90"), 1L, null);
    }

    private ProdutoUpdateRequest updateRequestComArquivo() {
        return new ProdutoUpdateRequest("Logo Atualizado", "Nova descrição",
                new BigDecimal("59.90"), 1L, 10L);
    }

    private ProdutoUpdateRequest updateRequestSemArquivo() {
        return new ProdutoUpdateRequest("Logo Atualizado", "Nova descrição",
                new BigDecimal("59.90"), 1L, null);
    }

    // =========================================================================
    // criar()
    // =========================================================================

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve criar produto com arquivo e retornar ProdutoResponse")
        void deveCriarProdutoComArquivo() {
            // Arrange
            ProdutoCreateRequest request = createRequestComArquivo();
            Categoria categoria = categoriaFake();
            Arquivo arquivo = arquivoAtivoFake();
            Produto produto = produtoFake();
            ProdutoResponse response = responseFake();

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
            when(arquivoRepository.findById(10L)).thenReturn(Optional.of(arquivo));
            when(mapper.toEntity(request)).thenReturn(produto);
            when(repository.save(produto)).thenReturn(produto);
            when(mapper.toResponse(produto)).thenReturn(response);

            // Act
            ProdutoResponse resultado = service.criar(request);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(100L);
            assertThat(resultado.arquivoId()).isEqualTo(10L);
            verify(repository).save(produto);
        }

        @Test
        @DisplayName("deve criar produto sem arquivo quando arquivoId é nulo")
        void deveCriarProdutoSemArquivo() {
            // Arrange
            ProdutoCreateRequest request = createRequestSemArquivo();
            Categoria categoria = categoriaFake();
            Produto produto = produtoFake();
            ProdutoResponse response = new ProdutoResponse(
                    100L, "Logo Premium", "Logotipo profissional",
                    new BigDecimal("49.90"), LocalDateTime.now(), 1L, "Design", null);

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
            when(mapper.toEntity(request)).thenReturn(produto);
            when(repository.save(produto)).thenReturn(produto);
            when(mapper.toResponse(produto)).thenReturn(response);

            // Act
            ProdutoResponse resultado = service.criar(request);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.arquivoId()).isNull();
            verify(arquivoRepository, never()).findById(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando categoria não existe")
        void deveLancarExcecaoQuandoCategoriaNaoExiste() {
            // Arrange
            ProdutoCreateRequest request = createRequestComArquivo();
            when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.criar(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Categoria")
                    .hasMessageContaining("1");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando arquivo não existe")
        void deveLancarExcecaoQuandoArquivoNaoExiste() {
            // Arrange
            ProdutoCreateRequest request = createRequestComArquivo();
            Categoria categoria = categoriaFake();
            Produto produto = produtoFake();

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
            when(mapper.toEntity(request)).thenReturn(produto);
            when(arquivoRepository.findById(10L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.criar(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Arquivo")
                    .hasMessageContaining("10");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando arquivo está inativo")
        void deveLancarExcecaoQuandoArquivoInativo() {
            // Arrange
            ProdutoCreateRequest request = new ProdutoCreateRequest(
                    "Logo", "Desc", new BigDecimal("10.00"), 1L, 99L);
            Categoria categoria = categoriaFake();
            Arquivo inativo = arquivoInativoFake();
            Produto produto = produtoFake();

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
            when(mapper.toEntity(request)).thenReturn(produto);
            when(arquivoRepository.findById(99L)).thenReturn(Optional.of(inativo));

            // Act & Assert
            assertThatThrownBy(() -> service.criar(request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                        assertThat(be.getMessage()).contains("99");
                        assertThat(be.getMessage()).contains("inativo");
                    });

            verify(repository, never()).save(any());
        }
    }

    // =========================================================================
    // buscarPorId()
    // =========================================================================

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar ProdutoResponse quando produto existe")
        void deveRetornarResponseQuandoProdutoExiste() {
            // Arrange
            Produto produto = produtoFake();
            ProdutoResponse response = responseFake();

            when(repository.findById(100L)).thenReturn(Optional.of(produto));
            when(mapper.toResponse(produto)).thenReturn(response);

            // Act
            ProdutoResponse resultado = service.buscarPorId(100L);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(100L);
            assertThat(resultado.titulo()).isEqualTo("Logo Premium");
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando produto não existe")
        void deveLancarExcecaoQuandoProdutoNaoExiste() {
            // Arrange
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.buscarPorId(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Produto")
                    .hasMessageContaining("999");
        }
    }

    // =========================================================================
    // listar()
    // =========================================================================

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("deve retornar página de ProdutoResponse")
        void deveRetornarPaginaDeProdutos() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Produto produto = produtoFake();
            ProdutoResponse response = responseFake();
            Page<Produto> page = new PageImpl<>(List.of(produto), pageable, 1);

            when(repository.findAll(pageable)).thenReturn(page);
            when(mapper.toResponse(produto)).thenReturn(response);

            // Act
            Page<ProdutoResponse> resultado = service.listar(pageable);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getTotalElements()).isEqualTo(1);
            assertThat(resultado.getContent().get(0).id()).isEqualTo(100L);
        }

        @Test
        @DisplayName("deve retornar página vazia quando não há produtos")
        void deveRetornarPaginaVazia() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Produto> page = new PageImpl<>(List.of(), pageable, 0);

            when(repository.findAll(pageable)).thenReturn(page);

            // Act
            Page<ProdutoResponse> resultado = service.listar(pageable);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getTotalElements()).isZero();
            assertThat(resultado.getContent()).isEmpty();
        }
    }

    // =========================================================================
    // listarPorCategoria()
    // =========================================================================

    @Nested
    @DisplayName("listarPorCategoria()")
    class ListarPorCategoria {

        @Test
        @DisplayName("deve retornar página filtrada pela categoria")
        void deveRetornarPaginaPorCategoria() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Categoria categoria = categoriaFake();
            Produto produto = produtoFake();
            ProdutoResponse response = responseFake();
            Page<Produto> page = new PageImpl<>(List.of(produto), pageable, 1);

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
            when(repository.findByCategoria_IdCategoria(1L, pageable)).thenReturn(page);
            when(mapper.toResponse(produto)).thenReturn(response);

            // Act
            Page<ProdutoResponse> resultado = service.listarPorCategoria(1L, pageable);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getTotalElements()).isEqualTo(1);
            assertThat(resultado.getContent().get(0).categoriaId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando categoria não existe")
        void deveLancarExcecaoQuandoCategoriaNaoExiste() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(categoriaRepository.findById(42L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.listarPorCategoria(42L, pageable))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Categoria")
                    .hasMessageContaining("42");

            verify(repository, never()).findByCategoria_IdCategoria(any(), any());
        }
    }

    // =========================================================================
    // atualizar()
    // =========================================================================

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar produto com arquivo e retornar ProdutoResponse")
        void deveAtualizarProdutoComArquivo() {
            // Arrange
            ProdutoUpdateRequest request = updateRequestComArquivo();
            Produto produto = produtoFake();
            Categoria categoria = categoriaFake();
            Arquivo arquivo = arquivoAtivoFake();
            ProdutoResponse response = responseFake();

            when(repository.findById(100L)).thenReturn(Optional.of(produto));
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
            when(arquivoRepository.findById(10L)).thenReturn(Optional.of(arquivo));
            when(repository.save(produto)).thenReturn(produto);
            when(mapper.toResponse(produto)).thenReturn(response);

            // Act
            ProdutoResponse resultado = service.atualizar(100L, request);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(100L);
            verify(mapper).updateEntityFromRequest(request, produto);
            verify(repository).save(produto);
        }

        @Test
        @DisplayName("deve remover arquivo do produto quando arquivoId é nulo no request")
        void deveRemoverArquivoQuandoArquivoIdNulo() {
            // Arrange
            ProdutoUpdateRequest request = updateRequestSemArquivo();
            Produto produto = produtoFake();
            Categoria categoria = categoriaFake();
            ProdutoResponse response = new ProdutoResponse(
                    100L, "Logo Atualizado", "Nova descrição",
                    new BigDecimal("59.90"), LocalDateTime.now(), 1L, "Design", null);

            when(repository.findById(100L)).thenReturn(Optional.of(produto));
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
            when(repository.save(produto)).thenReturn(produto);
            when(mapper.toResponse(produto)).thenReturn(response);

            // Act
            ProdutoResponse resultado = service.atualizar(100L, request);

            // Assert
            assertThat(resultado.arquivoId()).isNull();
            verify(arquivoRepository, never()).findById(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando produto não existe")
        void deveLancarExcecaoQuandoProdutoNaoExiste() {
            // Arrange
            ProdutoUpdateRequest request = updateRequestComArquivo();
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.atualizar(999L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Produto")
                    .hasMessageContaining("999");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando categoria não existe")
        void deveLancarExcecaoQuandoCategoriaNaoExiste() {
            // Arrange
            ProdutoUpdateRequest request = updateRequestComArquivo();
            Produto produto = produtoFake();

            when(repository.findById(100L)).thenReturn(Optional.of(produto));
            when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.atualizar(100L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Categoria")
                    .hasMessageContaining("1");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando arquivo não existe")
        void deveLancarExcecaoQuandoArquivoNaoExiste() {
            // Arrange
            ProdutoUpdateRequest request = updateRequestComArquivo();
            Produto produto = produtoFake();
            Categoria categoria = categoriaFake();

            when(repository.findById(100L)).thenReturn(Optional.of(produto));
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
            when(arquivoRepository.findById(10L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.atualizar(100L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Arquivo")
                    .hasMessageContaining("10");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando arquivo está inativo")
        void deveLancarExcecaoQuandoArquivoInativo() {
            // Arrange
            ProdutoUpdateRequest request = new ProdutoUpdateRequest(
                    "Logo", "Desc", new BigDecimal("10.00"), 1L, 99L);
            Produto produto = produtoFake();
            Categoria categoria = categoriaFake();
            Arquivo inativo = arquivoInativoFake();

            when(repository.findById(100L)).thenReturn(Optional.of(produto));
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
            when(arquivoRepository.findById(99L)).thenReturn(Optional.of(inativo));

            // Act & Assert
            assertThatThrownBy(() -> service.atualizar(100L, request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                        assertThat(be.getMessage()).contains("99");
                        assertThat(be.getMessage()).contains("inativo");
                    });

            verify(repository, never()).save(any());
        }
    }

    // =========================================================================
    // deletar()
    // =========================================================================

    @Nested
    @DisplayName("deletar()")
    class Deletar {

        @Test
        @DisplayName("deve deletar produto quando ele existe")
        void deveDeletarProdutoExistente() {
            // Arrange
            Produto produto = produtoFake();
            when(repository.findById(100L)).thenReturn(Optional.of(produto));

            // Act
            service.deletar(100L);

            // Assert
            verify(repository).delete(produto);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando produto não existe")
        void deveLancarExcecaoQuandoProdutoNaoExiste() {
            // Arrange
            when(repository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> service.deletar(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Produto")
                    .hasMessageContaining("999");

            verify(repository, never()).delete(any());
        }
    }
}