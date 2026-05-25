package com.bndesigner.exceptions.custom;

import org.springframework.http.HttpStatus;

public class ProdutoSemArquivoException extends BusinessException {

    public ProdutoSemArquivoException(String tituloProduto) {
        super(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Produto inválido",
            String.format(
                "Produto '%s' não possui arquivo associado.",
                tituloProduto
            )
        );
    }
}
