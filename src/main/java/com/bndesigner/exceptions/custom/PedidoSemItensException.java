package com.bndesigner.exceptions.custom;

import org.springframework.http.HttpStatus;

public class PedidoSemItensException extends BusinessException {

    public PedidoSemItensException() {
        super(
            HttpStatus.BAD_REQUEST,
            "Pedido inválido",
            "O pedido deve possuir ao menos um item."
        );
    }
}