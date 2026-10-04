package com.bndesigner.exceptions.custom;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;

import com.bndesigner.exceptions.handler.BusinessException;

public class ValorPagamentoInvalidoException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public ValorPagamentoInvalidoException(
            Long pedidoId,
            BigDecimal valorPedido,
            BigDecimal valorPagamento) {

        super(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Regra de negócio violada",
            "O valor do pagamento do pedido com id %d é inválido. "
                + "Valor do pedido: %s. Valor informado: %s."
                .formatted(
                    pedidoId,
                    valorPedido,
                    valorPagamento
                )
        );
    }
}