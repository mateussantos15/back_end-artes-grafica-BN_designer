package com.bndesigner.domain.validation;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.bndesigner.domain.entity.pagamento.Pagamento;
import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.domain.enums.pagamento.StatusPagamento;
import com.bndesigner.exceptions.custom.PagamentoJaExisteException;
import com.bndesigner.exceptions.custom.ValorPagamentoInvalidoException;
import com.bndesigner.repository.pagamento.PagamentoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PagamentoValidator {

    private final PagamentoRepository pagamentoRepository;

    public void validarNovoPagamento(Pedido pedido) {

        if (pagamentoRepository.existsByPedidoIdPedido(
                pedido.getIdPedido())) {

            throw new PagamentoJaExisteException(
                    pedido.getIdPedido());
        }
    }

    public void validarValor(Pedido pedido, BigDecimal valorPagamento) {

        if (valorPagamento == null
                || pedido.getValorTotal() == null
                || valorPagamento.compareTo(pedido.getValorTotal()) != 0) {

            throw new ValorPagamentoInvalidoException(
                    pedido.getIdPedido(),
                    pedido.getValorTotal(),
                    valorPagamento);
        }
    }
}