package com.bndesigner.exceptions.custom;

import org.springframework.http.HttpStatus;

import com.bndesigner.exceptions.handler.BusinessException;

public class UsuarioNaoComprouProdutoException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public UsuarioNaoComprouProdutoException(Long usuarioId, Long produtoId) {
        super(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Regra de negócio violada",
            "O usuário com id %d não possui uma compra do produto com id %d."
                .formatted(usuarioId, produtoId)
        );
    }
}
