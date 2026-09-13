package com.bndesigner.repository.metodopagamento;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.bndesigner.config.JpaAuditingConfig;
import com.bndesigner.domain.entity.metodopagamento.MetodoPagamento;
import com.bndesigner.repository.metodopagamento.MetodoPagamentoRepository;

@Import(JpaAuditingConfig.class)
@DataJpaTest
@DisplayName("MetodoPagamentoRepository")
class MetodoPagamentoRepositoryTest {

    @Autowired
    private MetodoPagamentoRepository metodoPagamentoRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private MetodoPagamento salvarMetodo(String nome, String descricao, Boolean ativo) {
        return metodoPagamentoRepository.save(
                MetodoPagamento.builder()
                        .nomeMetodoPagamento(nome)
                        .descricaoMetodoPagamento(descricao)
                        .ativo(ativo)
                        .build());
    }

    // =========================================================================
    // findByAtivoTrue()
    // =========================================================================

    @Nested
    @DisplayName("findByAtivoTrue()")
    class FindByAtivoTrue {

        @Test
        @DisplayName("deve retornar apenas métodos de pagamento ativos")
        void deveRetornarApenasAtivos() {
            // Arrange
            salvarMetodo("PIX", "Pagamento via Pix", true);
            salvarMetodo("Cartão de crédito", "Pagamento com cartão de crédito", true);
            salvarMetodo("Boleto", "Pagamento via boleto", false);

            // Act
            List<MetodoPagamento> resultado = metodoPagamentoRepository.findByAtivoTrue();

            // Assert
            assertThat(resultado).hasSize(2);
            assertThat(resultado)
                    .extracting(MetodoPagamento::getNomeMetodoPagamento)
                    .containsExactlyInAnyOrder(
                            "PIX",
                            "Cartão de crédito");
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não existem métodos ativos")
        void deveRetornarVazioQuandoNaoExistemAtivos() {
            // Arrange
            salvarMetodo("PIX", "Pagamento via Pix", false);
            salvarMetodo("Boleto", "Pagamento via boleto", false);

            // Act
            List<MetodoPagamento> resultado = metodoPagamentoRepository.findByAtivoTrue();

            // Assert
            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("deve retornar lista vazia quando repositório está vazio")
        void deveRetornarVazioQuandoRepositorioVazio() {
            // Act
            List<MetodoPagamento> resultado = metodoPagamentoRepository.findByAtivoTrue();

            // Assert
            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("não deve retornar métodos inativos")
        void naoDeveRetornarInativos() {
            // Arrange
            MetodoPagamento ativo = salvarMetodo(
                    "PIX",
                    "Pagamento via Pix",
                    true);

            MetodoPagamento inativo = salvarMetodo(
                    "Boleto",
                    "Pagamento via boleto",
                    false);

            // Act
            List<MetodoPagamento> resultado = metodoPagamentoRepository.findByAtivoTrue();

            // Assert
            assertThat(resultado)
                    .extracting(MetodoPagamento::getIdMetodoPagamento)
                    .contains(ativo.getIdMetodoPagamento())
                    .doesNotContain(inativo.getIdMetodoPagamento());
        }
    }

    // =========================================================================
    // Operações herdadas de JpaRepository
    // =========================================================================

    @Nested
    @DisplayName("Operações JPA básicas")
    class OperacoesBasicas {

        @Test
        @DisplayName("deve persistir método de pagamento e gerar id automaticamente")
        void devePersistirEGerarId() {
            // Act
            MetodoPagamento salvo = salvarMetodo(
                    "PIX",
                    "Pagamento via Pix",
                    true);

            // Assert
            assertThat(salvo.getIdMetodoPagamento())
                    .isNotNull()
                    .isPositive();
        }

        @Test
        @DisplayName("deve encontrar método de pagamento pelo id")
        void deveEncontrarPorId() {
            // Arrange
            MetodoPagamento salvo = salvarMetodo(
                    "PIX",
                    "Pagamento via Pix",
                    true);

            // Act & Assert
            assertThat(metodoPagamentoRepository.findById(salvo.getIdMetodoPagamento()))
                    .isPresent()
                    .contains(salvo);
        }

        @Test
        @DisplayName("deve retornar Optional vazio para id inexistente")
        void deveRetornarVazioParaIdInexistente() {
            // Act & Assert
            assertThat(metodoPagamentoRepository.findById(9999L))
                    .isEmpty();
        }

        @Test
        @DisplayName("deve retornar todos os métodos de pagamento")
        void deveRetornarTodos() {
            // Arrange
            salvarMetodo("PIX", "Pagamento via Pix", true);
            salvarMetodo("Cartão de crédito", "Pagamento com cartão de crédito", true);
            salvarMetodo("Boleto", "Pagamento via boleto", false);

            // Act
            List<MetodoPagamento> resultado = metodoPagamentoRepository.findAll();

            // Assert
            assertThat(resultado).hasSize(3);
        }

        @Test
        @DisplayName("deve deletar método de pagamento existente")
        void deveDeletarMetodoPagamento() {
            // Arrange
            MetodoPagamento salvo = salvarMetodo(
                    "PIX",
                    "Pagamento via Pix",
                    true);

            Long id = salvo.getIdMetodoPagamento();

            // Act
            metodoPagamentoRepository.delete(salvo);

            // Assert
            assertThat(metodoPagamentoRepository.findById(id))
                    .isEmpty();
        }
    }
}