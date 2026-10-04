package com.bndesigner.exceptions.custom;

import org.springframework.http.HttpStatus;

import com.bndesigner.exceptions.handler.BusinessException;

public class ItemComQuantidadeInvalidaException extends BusinessException {
	
	public ItemComQuantidadeInvalidaException(Long produtoId) {
		super(HttpStatus.BAD_REQUEST,
	            "Quantidade inválida",
	            String.format(
	                "Quantidade inválida para produto %d.",
	                produtoId));
	}

}
