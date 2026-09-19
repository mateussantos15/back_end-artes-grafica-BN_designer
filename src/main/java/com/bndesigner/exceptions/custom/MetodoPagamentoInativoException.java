package com.bndesigner.exceptions.custom;

import org.springframework.http.HttpStatus;

public class MetodoPagamentoInativoException extends BusinessException {

    public MetodoPagamentoInativoException(Long metodoPagamentoId) {
        super(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Método de pagamento inválido",
            String.format(
                "Método de pagamento com id %d está inativo e não pode ser utilizado.",
                metodoPagamentoId
            )
        );
    }
}