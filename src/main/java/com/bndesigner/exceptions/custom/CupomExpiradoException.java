package com.bndesigner.exceptions.custom;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;

public class CupomExpiradoException extends BusinessException {

    public CupomExpiradoException(String codigo, LocalDate dataValidade) {
        super(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Cupom expirado",
            String.format(
                "O cupom '%s' expirou em %s.",
                codigo,
                dataValidade.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            )
        );
    }
}
