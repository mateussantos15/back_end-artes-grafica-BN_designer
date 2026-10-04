package com.bndesigner.domain.entity.validation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.domain.validation.PagamentoValidator;
import com.bndesigner.exceptions.custom.PagamentoJaExisteException;
import com.bndesigner.exceptions.custom.ValorPagamentoInvalidoException;
import com.bndesigner.repository.pagamento.PagamentoRepository;

@ExtendWith(MockitoExtension.class)
class PagamentoValidatorTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    private PagamentoValidator pagamentoValidator;

    @BeforeEach
    void setUp() {
        pagamentoValidator =
                new PagamentoValidator(pagamentoRepository);
    }

    @Test
    void devePermitirNovoPagamentoQuandoPedidoNaoPossuiPagamento() {

        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .valorTotal(new BigDecimal("100.00"))
                .build();

        when(pagamentoRepository.existsByPedidoIdPedido(1L))
                .thenReturn(false);

        assertThatCode(() ->
                pagamentoValidator.validarNovoPagamento(pedido))
                .doesNotThrowAnyException();

        verify(pagamentoRepository)
                .existsByPedidoIdPedido(1L);
    }

    @Test
    void deveLancarExcecaoQuandoPedidoJaPossuiPagamento() {

        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .valorTotal(new BigDecimal("100.00"))
                .build();

        when(pagamentoRepository.existsByPedidoIdPedido(1L))
                .thenReturn(true);

        assertThatThrownBy(() ->
                pagamentoValidator.validarNovoPagamento(pedido))
                .isInstanceOf(PagamentoJaExisteException.class);

        verify(pagamentoRepository)
                .existsByPedidoIdPedido(1L);
    }

    @Test
    void devePermitirPagamentoQuandoValorForIgualAoPedido() {

        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .valorTotal(new BigDecimal("100.00"))
                .build();

        BigDecimal valorPagamento =
                new BigDecimal("100.00");

        assertThatCode(() ->
                pagamentoValidator.validarValor(
                        pedido,
                        valorPagamento))
                .doesNotThrowAnyException();
    }

    @Test
    void deveLancarExcecaoQuandoValorForDiferenteDoPedido() {

        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .valorTotal(new BigDecimal("100.00"))
                .build();

        BigDecimal valorPagamento =
                new BigDecimal("90.00");

        assertThatThrownBy(() ->
                pagamentoValidator.validarValor(
                        pedido,
                        valorPagamento))
                .isInstanceOf(ValorPagamentoInvalidoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoValorForNulo() {

        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .valorTotal(new BigDecimal("100.00"))
                .build();

        assertThatThrownBy(() ->
                pagamentoValidator.validarValor(
                        pedido,
                        null))
                .isInstanceOf(ValorPagamentoInvalidoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoValorDoPedidoForNulo() {

        Pedido pedido = Pedido.builder()
                .idPedido(1L)
                .valorTotal(null)
                .build();

        assertThatThrownBy(() ->
                pagamentoValidator.validarValor(
                        pedido,
                        new BigDecimal("100.00")))
                .isInstanceOf(ValorPagamentoInvalidoException.class);
    }
}