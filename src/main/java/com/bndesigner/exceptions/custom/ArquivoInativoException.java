package com.bndesigner.exceptions.custom;

import org.springframework.http.HttpStatus;

import com.bndesigner.exceptions.handler.BusinessException;

public class ArquivoInativoException extends BusinessException {
	
	private static final long serialVersionUID = 1L;

	public ArquivoInativoException(Long identifier) {
		
		super(HttpStatus.UNPROCESSABLE_ENTITY,
                "Regra de negócio violada",
                "Arquivo com id %d está inativo e não pode ser associado.".
                formatted(identifier));
		
	}

}
