package com.bndesigner.exceptions.custom;

import org.springframework.http.HttpStatus;

import com.bndesigner.exceptions.handler.BusinessException;

public class CupomInativoException extends BusinessException {

    public CupomInativoException(String codigo) {
        super(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Cupom inativo",
            String.format(
                "O cupom '%s' não está ativo e não pode ser utilizado.",
                codigo
            )
        );
    }
}
