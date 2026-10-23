package com.bndesigner.exceptions.custom;

import org.springframework.http.HttpStatus;

import com.bndesigner.exceptions.handler.BusinessException;

public class AvaliacaoJaExisteException extends BusinessException {

	private static final long serialVersionUID = 1L;

	public AvaliacaoJaExisteException(Long usuarioId, Long produtoId) {
		super(
				HttpStatus.UNPROCESSABLE_ENTITY,
				"Regra de negócio violada", 
				"O usuário com id %d já possui uma avaliação para o produto com id %d."
				.formatted(usuarioId, produtoId));
	}

}
