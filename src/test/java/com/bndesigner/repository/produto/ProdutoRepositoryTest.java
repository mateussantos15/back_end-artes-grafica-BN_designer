package com.bndesigner.repository.produto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.bndesigner.domain.entity.arquivo.Arquivo;
import com.bndesigner.domain.entity.categoria.Categoria;
import com.bndesigner.domain.entity.produto.Produto;
import com.bndesigner.repository.arquivo.ArquivoRepository;
import com.bndesigner.repository.categoria.CategoriaRepository;

@DataJpaTest
@DisplayName("ProdutoRepository")
class ProdutoRepositoryTest {

    // ─────────────────────────────────────────────────────────────────────────
    // Repositórios injetados
    // ─────────────────────────────────────────────────────────────────────────

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ArquivoRepository arquivoRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // Fixtures — recriadas antes de cada teste pelo @BeforeEach
    // ─────────────────────────────────────────────────────────────────────────

    private Categoria categoriaDesign;
    private Categoria categoriaMarketing;
    private Arquivo arquivoAtivo;

    @BeforeEach
    void setUp() {
        categoriaDesign = categoriaRepository.save(
                Categoria.builder().nome("Design").descricao("Produtos de design").build());

        categoriaMarketing = categoriaRepository.save(
                Categoria.builder().nome("Marketing").descricao("Produtos de marketing").build());

        arquivoAtivo = arquivoRepository.save(
                Arquivo.builder()
                        .caminhoArquivo("/uploads/logo.png")
                        .hashArquivo("abc123")
                        .ativo(true)
                        .build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private Produto salvarProduto(String titulo, BigDecimal preco, Categoria categoria, Arquivo arquivo) {
        return produtoRepository.save(
                Produto.builder()
                        .titulo(titulo)
                        .descricao("Descrição de " + titulo)
                        .preco(preco)
                        .categoria(categoria)
                        .arquivo(arquivo)
                        .build());
    }

    private Produto salvarProduto(String titulo, Categoria categoria) {
        return salvarProduto(titulo, new BigDecimal("49.90"), categoria, null);
    }

    // =========================================================================
    // existsByTituloIgnoreCase()
    // =========================================================================

    @Nested
    @DisplayName("existsByTituloIgnoreCase()")
    class ExistsByTituloIgnoreCase {

        @Test
        @DisplayName("deve retornar true quando título existe com mesma capitalização")
        void deveRetornarTrueComMesmaCaps() {
            // Arrange
            salvarProduto("Logo Premium", categoriaDesign);

            // Act & Assert
            assertThat(produtoRepository.existsByTituloIgnoreCase("Logo Premium")).isTrue();
        }

        @Test
        @DisplayName("deve retornar true quando título existe com capitalização diferente")
        void deveRetornarTrueComCapsVariada() {
            // Arrange
            salvarProduto("Logo Premium", categoriaDesign);

            // Act & Assert
            assertThat(produtoRepository.existsByTituloIgnoreCase("logo premium")).isTrue();
            assertThat(produtoRepository.existsByTituloIgnoreCase("LOGO PREMIUM")).isTrue();
            assertThat(produtoRepository.existsByTituloIgnoreCase("Logo PREMIUM")).isTrue();
        }

        @Test
        @DisplayName("deve retornar false quando título não existe")
        void deveRetornarFalseQuandoNaoExiste() {
            // Arrange
            salvarProduto("Logo Premium", categoriaDesign);

            // Act & Assert
            assertThat(produtoRepository.existsByTituloIgnoreCase("Banner Corporativo")).isFalse();
        }

        @Test
        @DisplayName("deve retornar false quando repositório está vazio")
        void deveRetornarFalseQuandoVazio() {
            // Act & Assert
            assertThat(produtoRepository.existsByTituloIgnoreCase("qualquer titulo")).isFalse();
        }
    }

    // =========================================================================
    // findAll(Pageable) — com @EntityGraph
    // =========================================================================

    @Nested
    @DisplayName("findAll(Pageable)")
    class FindAll {

        @Test
        @DisplayName("deve retornar todos os produtos paginados")
        void deveRetornarTodosPaginados() {
            // Arrange
            salvarProduto("Logo Premium", new BigDecimal("49.90"), categoriaDesign, arquivoAtivo);
            salvarProduto("Banner Corporativo", new BigDecimal("89.90"), categoriaMarketing, null);
            salvarProduto("Kit Identidade Visual", new BigDecimal("199.90"), categoriaDesign, null);

            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<Produto> resultado = produtoRepository.findAll(pageable);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getTotalElements()).isEqualTo(3);
            assertThat(resultado.getContent()).hasSize(3);
        }

        @Test
        @DisplayName("deve respeitar o tamanho da página")
        void deveRespeitarTamanhoPagina() {
            // Arrange
            salvarProduto("Produto A", categoriaDesign);
            salvarProduto("Produto B", categoriaDesign);
            salvarProduto("Produto C", categoriaDesign);

            Pageable pageable = PageRequest.of(0, 2);

            // Act
            Page<Produto> resultado = produtoRepository.findAll(pageable);

            // Assert
            assertThat(resultado.getContent()).hasSize(2);
            assertThat(resultado.getTotalElements()).isEqualTo(3);
            assertThat(resultado.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("deve retornar página vazia quando não há produtos")
        void deveRetornarPaginaVazia() {
            // Act
            Page<Produto> resultado = produtoRepository.findAll(PageRequest.of(0, 10));

            // Assert
            assertThat(resultado.getContent()).isEmpty();
            assertThat(resultado.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("deve carregar associação categoria via EntityGraph (sem N+1)")
        void deveCarregarCategoriaViaEntityGraph() {
            // Arrange
            salvarProduto("Logo Premium", new BigDecimal("49.90"), categoriaDesign, null);

            // Act
            Page<Produto> resultado = produtoRepository.findAll(PageRequest.of(0, 10));

            // Assert — categoria deve estar inicializada (não lazy) sem precisar de sessão aberta
            Produto produto = resultado.getContent().get(0);
            assertThat(produto.getCategoria()).isNotNull();
            assertThat(produto.getCategoria().getNome()).isEqualTo("Design");
        }

        @Test
        @DisplayName("deve carregar associação arquivo via EntityGraph quando presente")
        void deveCarregarArquivoViaEntityGraph() {
            // Arrange
            salvarProduto("Logo Premium", new BigDecimal("49.90"), categoriaDesign, arquivoAtivo);

            // Act
            Page<Produto> resultado = produtoRepository.findAll(PageRequest.of(0, 10));

            // Assert
            Produto produto = resultado.getContent().get(0);
            assertThat(produto.getArquivo()).isNotNull();
            assertThat(produto.getArquivo().getCaminhoArquivo()).isEqualTo("/uploads/logo.png");
        }

        @Test
        @DisplayName("deve retornar arquivo nulo para produto sem arquivo associado")
        void deveRetornarArquivoNuloQuandoAusente() {
            // Arrange
            salvarProduto("Logo Sem Arquivo", categoriaDesign);

            // Act
            Page<Produto> resultado = produtoRepository.findAll(PageRequest.of(0, 10));

            // Assert
            assertThat(resultado.getContent().get(0).getArquivo()).isNull();
        }
    }

    // =========================================================================
    // findByCategoria_Id(Long, Pageable) — com @EntityGraph
    // =========================================================================

    @Nested
    @DisplayName("findByCategoria_Id()")
    class FindByCategoria {

        @Test
        @DisplayName("deve retornar apenas produtos da categoria informada")
        void deveRetornarProdutosDaCategoria() {
            // Arrange
            salvarProduto("Logo Premium", categoriaDesign);
            salvarProduto("Kit Visual", categoriaDesign);
            salvarProduto("Banner Ads", categoriaMarketing);

            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<Produto> resultado = produtoRepository
                    .findByCategoria_IdCategoria(categoriaDesign.getIdCategoria(), pageable);

            // Assert
            assertThat(resultado.getTotalElements()).isEqualTo(2);
            assertThat(resultado.getContent())
                    .extracting(p -> p.getCategoria().getIdCategoria())
                    .containsOnly(categoriaDesign.getIdCategoria());
        }

        @Test
        @DisplayName("deve retornar página vazia quando categoria não tem produtos")
        void deveRetornarVazioParaCategoriaSemProdutos() {
            // Arrange — apenas produto em categoriaDesign
            salvarProduto("Logo Premium", categoriaDesign);

            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<Produto> resultado = produtoRepository
                    .findByCategoria_IdCategoria(categoriaMarketing.getIdCategoria(), pageable);

            // Assert
            assertThat(resultado.getContent()).isEmpty();
            assertThat(resultado.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("deve retornar página vazia quando categoria não existe")
        void deveRetornarVazioParaCategoriaInexistente() {
            // Arrange
            salvarProduto("Logo Premium", categoriaDesign);

            // Act
            Page<Produto> resultado = produtoRepository
                    .findByCategoria_IdCategoria(9999L, PageRequest.of(0, 10));

            // Assert
            assertThat(resultado.getContent()).isEmpty();
        }

        @Test
        @DisplayName("deve respeitar paginação ao filtrar por categoria")
        void deveRespeitarPaginacaoPorCategoria() {
            // Arrange
            salvarProduto("Produto Design A", categoriaDesign);
            salvarProduto("Produto Design B", categoriaDesign);
            salvarProduto("Produto Design C", categoriaDesign);

            Pageable pageable = PageRequest.of(0, 2);

            // Act
            Page<Produto> resultado = produtoRepository
                    .findByCategoria_IdCategoria(categoriaDesign.getIdCategoria(), pageable);

            // Assert
            assertThat(resultado.getContent()).hasSize(2);
            assertThat(resultado.getTotalElements()).isEqualTo(3);
            assertThat(resultado.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("deve carregar categoria e arquivo via EntityGraph ao filtrar por categoria")
        void deveCarregarAssociacoesViaEntityGraph() {
            // Arrange
            salvarProduto("Logo Premium", new BigDecimal("49.90"), categoriaDesign, arquivoAtivo);

            // Act
            Page<Produto> resultado = produtoRepository
                    .findByCategoria_IdCategoria(categoriaDesign.getIdCategoria(), PageRequest.of(0, 10));

            // Assert
            Produto produto = resultado.getContent().get(0);
            assertThat(produto.getCategoria()).isNotNull();
            assertThat(produto.getCategoria().getNome()).isEqualTo("Design");
            assertThat(produto.getArquivo()).isNotNull();
            assertThat(produto.getArquivo().getAtivo()).isTrue();
        }

        @Test
        @DisplayName("deve isolar corretamente produtos entre categorias distintas")
        void deveIsolarprodutosEntreCategorias() {
            // Arrange
            salvarProduto("Logo Design", categoriaDesign);
            salvarProduto("Banner Marketing", categoriaMarketing);

            // Act
            List<Produto> design = produtoRepository
                    .findByCategoria_IdCategoria(categoriaDesign.getIdCategoria(), PageRequest.of(0, 10))
                    .getContent();

            List<Produto> marketing = produtoRepository
                    .findByCategoria_IdCategoria(categoriaMarketing.getIdCategoria(), PageRequest.of(0, 10))
                    .getContent();

            // Assert
            assertThat(design).hasSize(1);
            assertThat(design.get(0).getTitulo()).isEqualTo("Logo Design");

            assertThat(marketing).hasSize(1);
            assertThat(marketing.get(0).getTitulo()).isEqualTo("Banner Marketing");
        }
    }

    // =========================================================================
    // Comportamento herdado de JpaRepository — save / findById / delete
    // =========================================================================

    @Nested
    @DisplayName("Operações JPA básicas")
    class OperacoesBasicas {

        @Test
        @DisplayName("deve persistir produto e gerar id automaticamente")
        void devePersistirEGerarId() {
            // Act
            Produto salvo = salvarProduto("Logo Premium", categoriaDesign);

            // Assert
            assertThat(salvo.getIdProduto()).isNotNull().isPositive();
        }

        @Test
        @DisplayName("deve preencher dataCadastro via @PrePersist")
        void devePreencherDataCadastro() {
            // Act
            Produto salvo = salvarProduto("Logo Premium", categoriaDesign);

            // Assert
            assertThat(salvo.getDataCadastro()).isNotNull();
        }

        @Test
        @DisplayName("deve encontrar produto pelo id")
        void deveEncontrarPorId() {
            // Arrange
            Produto salvo = salvarProduto("Logo Premium", categoriaDesign);

            // Act & Assert
            assertThat(produtoRepository.findById(salvo.getIdProduto())).isPresent();
        }

        @Test
        @DisplayName("deve retornar Optional vazio para id inexistente")
        void deveRetornarVazioParaIdInexistente() {
            assertThat(produtoRepository.findById(9999L)).isEmpty();
        }

        @Test
        @DisplayName("deve deletar produto existente")
        void deveDeletarProduto() {
            // Arrange
            Produto salvo = salvarProduto("Logo Premium", categoriaDesign);
            Long id = salvo.getIdProduto();

            // Act
            produtoRepository.delete(salvo);

            // Assert
            assertThat(produtoRepository.findById(id)).isEmpty();
        }
    }
}
