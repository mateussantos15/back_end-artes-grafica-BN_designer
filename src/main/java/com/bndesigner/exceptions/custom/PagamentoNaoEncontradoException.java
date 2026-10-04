package com.bndesigner.exceptions.custom;

import org.springframework.http.HttpStatus;

import com.bndesigner.exceptions.handler.BusinessException;

public class PagamentoNaoEncontradoException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public PagamentoNaoEncontradoException(Long pedidoId) {
        super(
            HttpStatus.NOT_FOUND,
            "Pagamento não encontrado",
            "Não existe pagamento para o pedido com id %d."
                .formatted(pedidoId)
        );
    }
}
