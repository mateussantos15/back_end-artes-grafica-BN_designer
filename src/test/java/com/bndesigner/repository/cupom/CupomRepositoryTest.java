package com.bndesigner.repository.cupom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.bndesigner.domain.entity.cupom.Cupom;
import com.bndesigner.domain.enums.cupom.StatusCupom;

@DataJpaTest
@DisplayName("CupomRepository")
class CupomRepositoryTest {

    // ─────────────────────────────────────────────────────────────────────────
    // Repositório
    // ─────────────────────────────────────────────────────────────────────────

    @Autowired
    private CupomRepository repository;

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private Cupom salvar(String codigo, StatusCupom status) {
        return repository.save(
                Cupom.builder()
                        .codigo(codigo)
                        .descontoPercentual(new BigDecimal("10.00"))
                        .dataValidade(LocalDate.now().plusDays(30))
                        .status(status)
                        .build());
    }

    private Cupom salvar(String codigo, StatusCupom status, BigDecimal desconto, LocalDate validade) {
        return repository.save(
                Cupom.builder()
                        .codigo(codigo)
                        .descontoPercentual(desconto)
                        .dataValidade(validade)
                        .status(status)
                        .build());
    }

    // =========================================================================
    // findByCodigoIgnoreCase()
    // =========================================================================

    @Nested
    @DisplayName("findByCodigoIgnoreCase()")
    class FindByCodigoIgnoreCase {

        @Test
        @DisplayName("deve retornar o cupom quando código existe com mesma capitalização")
        void deveRetornarCupomComMesmaCaps() {
            // Arrange
            salvar("PROMO10", StatusCupom.ATIVO);

            // Act
            Optional<Cupom> resultado = repository.findByCodigoIgnoreCase("PROMO10");

            // Assert
            assertThat(resultado).isPresent();
            assertThat(resultado.get().getCodigo()).isEqualTo("PROMO10");
        }

        @Test
        @DisplayName("deve retornar o cupom quando código existe em minúsculas")
        void deveRetornarCupomComMinusculas() {
            // Arrange
            salvar("PROMO10", StatusCupom.ATIVO);

            // Act
            Optional<Cupom> resultado = repository.findByCodigoIgnoreCase("promo10");

            // Assert
            assertThat(resultado).isPresent();
            assertThat(resultado.get().getCodigo()).isEqualTo("PROMO10");
        }

        @Test
        @DisplayName("deve retornar o cupom quando código existe com capitalização mista")
        void deveRetornarCupomComCapsVariada() {
            // Arrange
            salvar("PROMO10", StatusCupom.ATIVO);

            // Act
            Optional<Cupom> resultado = repository.findByCodigoIgnoreCase("Promo10");

            // Assert
            assertThat(resultado).isPresent();
        }

        @Test
        @DisplayName("deve retornar Optional vazio quando código não existe")
        void deveRetornarVazioQuandoCodigoNaoExiste() {
            // Arrange
            salvar("PROMO10", StatusCupom.ATIVO);

            // Act
            Optional<Cupom> resultado = repository.findByCodigoIgnoreCase("INEXISTENTE");

            // Assert
            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("deve retornar Optional vazio quando repositório está vazio")
        void deveRetornarVazioQuandoRepositorioVazio() {
            // Act
            Optional<Cupom> resultado = repository.findByCodigoIgnoreCase("PROMO10");

            // Assert
            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("deve retornar todos os campos do cupom corretamente")
        void deveRetornarTodosCampos() {
            // Arrange
            LocalDate validade = LocalDate.of(2026, 12, 31);
            salvar("BLACK50", StatusCupom.ATIVO, new BigDecimal("50.00"), validade);

            // Act
            Cupom resultado = repository.findByCodigoIgnoreCase("BLACK50").orElseThrow();

            // Assert
            assertThat(resultado.getId()).isNotNull().isPositive();
            assertThat(resultado.getCodigo()).isEqualTo("BLACK50");
            assertThat(resultado.getDescontoPercentual()).isEqualByComparingTo("50.00");
            assertThat(resultado.getDataValidade()).isEqualTo(validade);
            assertThat(resultado.getStatus()).isEqualTo(StatusCupom.ATIVO);
        }
    }

    // =========================================================================
    // existsByCodigoIgnoreCase()
    // =========================================================================

    @Nested
    @DisplayName("existsByCodigoIgnoreCase()")
    class ExistsByCodigoIgnoreCase {

        @Test
        @DisplayName("deve retornar true quando código existe com mesma capitalização")
        void deveRetornarTrueComMesmaCaps() {
            // Arrange
            salvar("PROMO10", StatusCupom.ATIVO);

            // Act & Assert
            assertThat(repository.existsByCodigoIgnoreCase("PROMO10")).isTrue();
        }

        @Test
        @DisplayName("deve retornar true quando código existe em minúsculas")
        void deveRetornarTrueComMinusculas() {
            // Arrange
            salvar("PROMO10", StatusCupom.ATIVO);

            // Act & Assert
            assertThat(repository.existsByCodigoIgnoreCase("promo10")).isTrue();
        }

        @Test
        @DisplayName("deve retornar true quando código existe com capitalização mista")
        void deveRetornarTrueComCapsVariada() {
            // Arrange
            salvar("PROMO10", StatusCupom.ATIVO);

            // Act & Assert
            assertThat(repository.existsByCodigoIgnoreCase("Promo10")).isTrue();
            assertThat(repository.existsByCodigoIgnoreCase("PROMO10")).isTrue();
            assertThat(repository.existsByCodigoIgnoreCase("promo10")).isTrue();
        }

        @Test
        @DisplayName("deve retornar false quando código não existe")
        void deveRetornarFalseQuandoNaoExiste() {
            // Arrange
            salvar("PROMO10", StatusCupom.ATIVO);

            // Act & Assert
            assertThat(repository.existsByCodigoIgnoreCase("DESCONTO20")).isFalse();
        }

        @Test
        @DisplayName("deve retornar false quando repositório está vazio")
        void deveRetornarFalseQuandoVazio() {
            // Act & Assert
            assertThat(repository.existsByCodigoIgnoreCase("PROMO10")).isFalse();
        }
    }

    // =========================================================================
    // findByStatus()
    // =========================================================================

    @Nested
    @DisplayName("findByStatus()")
    class FindByStatus {

        @Test
        @DisplayName("deve retornar apenas cupons com status ATIVO")
        void deveRetornarApenasAtivos() {
            // Arrange
            salvar("ATIVO1", StatusCupom.ATIVO);
            salvar("ATIVO2", StatusCupom.ATIVO);
            salvar("EXPIRADO1", StatusCupom.EXPIRADO);
            salvar("INATIVO1", StatusCupom.INATIVO);

            // Act
            Page<Cupom> resultado = repository.findByStatus(StatusCupom.ATIVO, PageRequest.of(0, 10));

            // Assert
            assertThat(resultado.getTotalElements()).isEqualTo(2);
            assertThat(resultado.getContent())
                    .extracting(Cupom::getStatus)
                    .containsOnly(StatusCupom.ATIVO);
        }

        @Test
        @DisplayName("deve retornar apenas cupons com status EXPIRADO")
        void deveRetornarApenasExpirados() {
            // Arrange
            salvar("ATIVO1", StatusCupom.ATIVO);
            salvar("EXPIRADO1", StatusCupom.EXPIRADO);
            salvar("EXPIRADO2", StatusCupom.EXPIRADO);

            // Act
            Page<Cupom> resultado = repository.findByStatus(StatusCupom.EXPIRADO, PageRequest.of(0, 10));

            // Assert
            assertThat(resultado.getTotalElements()).isEqualTo(2);
            assertThat(resultado.getContent())
                    .extracting(Cupom::getStatus)
                    .containsOnly(StatusCupom.EXPIRADO);
        }

        @Test
        @DisplayName("deve retornar apenas cupons com status INATIVO")
        void deveRetornarApenasInativos() {
            // Arrange
            salvar("ATIVO1", StatusCupom.ATIVO);
            salvar("INATIVO1", StatusCupom.INATIVO);

            // Act
            Page<Cupom> resultado = repository.findByStatus(StatusCupom.INATIVO, PageRequest.of(0, 10));

            // Assert
            assertThat(resultado.getTotalElements()).isEqualTo(1);
            assertThat(resultado.getContent().get(0).getCodigo()).isEqualTo("INATIVO1");
        }

        @Test
        @DisplayName("deve retornar página vazia quando nenhum cupom tem o status informado")
        void deveRetornarVazioQuandoNenhumComStatus() {
            // Arrange
            salvar("ATIVO1", StatusCupom.ATIVO);

            // Act
            Page<Cupom> resultado = repository.findByStatus(StatusCupom.EXPIRADO, PageRequest.of(0, 10));

            // Assert
            assertThat(resultado.getContent()).isEmpty();
            assertThat(resultado.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("deve retornar página vazia quando repositório está vazio")
        void deveRetornarVazioQuandoRepositorioVazio() {
            // Act
            Page<Cupom> resultado = repository.findByStatus(StatusCupom.ATIVO, PageRequest.of(0, 10));

            // Assert
            assertThat(resultado.getContent()).isEmpty();
        }

        @Test
        @DisplayName("deve isolar corretamente os três status distintos")
        void deveIsolarcorretamenteOsTresStatus() {
            // Arrange
            salvar("ATIVO1", StatusCupom.ATIVO);
            salvar("EXPIRADO1", StatusCupom.EXPIRADO);
            salvar("INATIVO1", StatusCupom.INATIVO);

            // Act
            long ativos     = repository.findByStatus(StatusCupom.ATIVO, PageRequest.of(0, 10)).getTotalElements();
            long expirados  = repository.findByStatus(StatusCupom.EXPIRADO, PageRequest.of(0, 10)).getTotalElements();
            long inativos   = repository.findByStatus(StatusCupom.INATIVO, PageRequest.of(0, 10)).getTotalElements();

            // Assert
            assertThat(ativos).isEqualTo(1);
            assertThat(expirados).isEqualTo(1);
            assertThat(inativos).isEqualTo(1);
        }
    }

    // =========================================================================
    // Constraints de banco
    // =========================================================================

    @Nested
    @DisplayName("Constraints de banco")
    class Constraints {

        @Test
        @DisplayName("deve lançar exceção ao persistir dois cupons com o mesmo código")
        void deveLancarExcecaoComCodigoDuplicado() {
            // Arrange
            salvar("PROMO10", StatusCupom.ATIVO);

            // Act & Assert
            assertThatThrownBy(() -> {
                repository.saveAndFlush(
                        Cupom.builder()
                                .codigo("PROMO10")
                                .descontoPercentual(new BigDecimal("5.00"))
                                .status(StatusCupom.INATIVO)
                                .build());
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("deve lançar exceção ao persistir cupom sem status")
        void deveLancarExcecaoSemStatus() {
            // Act & Assert
            assertThatThrownBy(() -> {
                repository.saveAndFlush(
                        Cupom.builder()
                                .codigo("SEM-STATUS")
                                .descontoPercentual(new BigDecimal("10.00"))
                                .build());
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("deve lançar exceção ao persistir cupom sem código")
        void deveLancarExcecaoSemCodigo() {
            // Act & Assert
            assertThatThrownBy(() -> {
                repository.saveAndFlush(
                        Cupom.builder()
                                .status(StatusCupom.ATIVO)
                                .descontoPercentual(new BigDecimal("10.00"))
                                .build());
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("deve persistir cupom sem descontoPercentual (campo opcional)")
        void devePersistirSemDesconto() {
            // Act
            Cupom salvo = repository.saveAndFlush(
                    Cupom.builder()
                            .codigo("SEM-DESCONTO")
                            .status(StatusCupom.ATIVO)
                            .build());

            // Assert
            assertThat(salvo.getId()).isNotNull();
            assertThat(salvo.getDescontoPercentual()).isNull();
        }

        @Test
        @DisplayName("deve persistir cupom sem dataValidade (campo opcional)")
        void devePersistirSemDataValidade() {
            // Act
            Cupom salvo = repository.saveAndFlush(
                    Cupom.builder()
                            .codigo("SEM-VALIDADE")
                            .status(StatusCupom.ATIVO)
                            .build());

            // Assert
            assertThat(salvo.getId()).isNotNull();
            assertThat(salvo.getDataValidade()).isNull();
        }
    }

    // =========================================================================
    // Operações JPA básicas
    // =========================================================================

    @Nested
    @DisplayName("Operações JPA básicas")
    class OperacoesBasicas {

        @Test
        @DisplayName("deve persistir cupom e gerar id automaticamente")
        void devePersistirEGerarId() {
            // Act
            Cupom salvo = salvar("PROMO10", StatusCupom.ATIVO);

            // Assert
            assertThat(salvo.getId()).isNotNull().isPositive();
        }

        @Test
        @DisplayName("deve encontrar cupom pelo id")
        void deveEncontrarPorId() {
            // Arrange
            Cupom salvo = salvar("PROMO10", StatusCupom.ATIVO);

            // Act & Assert
            assertThat(repository.findById(salvo.getId())).isPresent();
        }

        @Test
        @DisplayName("deve retornar Optional vazio para id inexistente")
        void deveRetornarVazioParaIdInexistente() {
            assertThat(repository.findById(9999L)).isEmpty();
        }

        @Test
        @DisplayName("deve deletar cupom existente")
        void deveDeletarCupom() {
            // Arrange
            Cupom salvo = salvar("PROMO10", StatusCupom.ATIVO);
            Long id = salvo.getId();

            // Act
            repository.delete(salvo);

            // Assert
            assertThat(repository.findById(id)).isEmpty();
        }

        @Test
        @DisplayName("deve persistir o enum StatusCupom como String no banco")
        void devePersistirEnumComoString() {
            // Arrange
            Cupom salvo = salvar("ENUM-TEST", StatusCupom.EXPIRADO);

            // Act — recarrega do banco para garantir que foi o que foi gravado
            Cupom recarregado = repository.findById(salvo.getId()).orElseThrow();

            // Assert
            assertThat(recarregado.getStatus()).isEqualTo(StatusCupom.EXPIRADO);
        }
    }
}
