package com.bndesigner.domain.entity.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bndesigner.domain.entity.metodopagamento.MetodoPagamento;
import com.bndesigner.domain.resolver.MetodoPagamentoResolver;
import com.bndesigner.exceptions.custom.MetodoPagamentoInativoException;
import com.bndesigner.exceptions.custom.ResourceNotFoundException;
import com.bndesigner.repository.metodopagamento.MetodoPagamentoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("MetodoPagamentoResolver")
class MetodoPagamentoResolverTest {

    @Mock
    private MetodoPagamentoRepository metodoPagamentoRepository;

    @InjectMocks
    private MetodoPagamentoResolver metodoPagamentoResolver;

    private MetodoPagamento metodoAtivo;
    private MetodoPagamento metodoInativo;

    @BeforeEach
    void setUp() {
        metodoAtivo = MetodoPagamento.builder()
                .idMetodoPagamento(1L)
                .nomeMetodoPagamento("PIX")
                .descricaoMetodoPagamento("Pagamento via Pix")
                .ativo(true)
                .build();

        metodoInativo = MetodoPagamento.builder()
                .idMetodoPagamento(2L)
                .nomeMetodoPagamento("Boleto")
                .descricaoMetodoPagamento("Pagamento via boleto")
                .ativo(false)
                .build();
    }

    // =========================================================================
    // buscarMetodoDisponivel()
    // =========================================================================

    @Nested
    @DisplayName("buscarMetodoDisponivel()")
    class BuscarMetodoDisponivel {

        @Test
        @DisplayName("deve retornar método quando existe e está ativo")
        void deveRetornarMetodoQuandoAtivo() {
            // Arrange
            when(metodoPagamentoRepository.findById(1L))
                    .thenReturn(Optional.of(metodoAtivo));

            // Act
            MetodoPagamento resultado =
                    metodoPagamentoResolver.buscarMetodoDisponivel(1L);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado).isSameAs(metodoAtivo);
            assertThat(resultado.getIdMetodoPagamento()).isEqualTo(1L);
            assertThat(resultado.getNomeMetodoPagamento()).isEqualTo("PIX");
            assertThat(resultado.getAtivo()).isTrue();

            verify(metodoPagamentoRepository).findById(1L);
        }

        @Test
        @DisplayName("deve lançar MetodoPagamentoInativoException quando método está inativo")
        void deveLancarExcecaoQuandoMetodoInativo() {
            // Arrange
            when(metodoPagamentoRepository.findById(2L))
                    .thenReturn(Optional.of(metodoInativo));

            // Act & Assert
            assertThatThrownBy(
                    () -> metodoPagamentoResolver.buscarMetodoDisponivel(2L))
                    .isInstanceOf(MetodoPagamentoInativoException.class);

            verify(metodoPagamentoRepository).findById(2L);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando método não existe")
        void deveLancarExcecaoQuandoMetodoNaoExiste() {
            // Arrange
            when(metodoPagamentoRepository.findById(9999L))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(
                    () -> metodoPagamentoResolver.buscarMetodoDisponivel(9999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(metodoPagamentoRepository).findById(9999L);
        }

        @Test
        @DisplayName("não deve retornar método inativo")
        void naoDeveRetornarMetodoInativo() {
            // Arrange
            when(metodoPagamentoRepository.findById(2L))
                    .thenReturn(Optional.of(metodoInativo));

            // Act & Assert
            assertThatThrownBy(
                    () -> metodoPagamentoResolver.buscarMetodoDisponivel(2L))
                    .isInstanceOf(MetodoPagamentoInativoException.class);

            verify(metodoPagamentoRepository, times(1))
                    .findById(2L);
        }
    }
}