package com.bndesigner.exceptions.custom;

import org.springframework.http.HttpStatus;

import com.bndesigner.exceptions.handler.BusinessException;

public class PagamentoJaExisteException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public PagamentoJaExisteException(Long pedidoId) {

        super(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Regra de negócio violada",
            "Já existe um pagamento para o pedido com id %d."
                .formatted(pedidoId)
        );
    }
}