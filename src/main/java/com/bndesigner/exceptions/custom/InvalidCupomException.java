package com.bndesigner.exceptions.custom;

import java.time.LocalDate;
import org.springframework.http.HttpStatus;

public class InvalidCupomException extends BusinessException {
	
	private static final long serialVersionUID = 1L;

	public InvalidCupomException(LocalDate dataValidade) {
		super(HttpStatus.BAD_REQUEST,
                "Cupom Expirado!",
                String.format("A validade do cupom expirou em: '%s'.", dataValidade));
	}

}
