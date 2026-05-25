package com.bndesigner.exceptions.custom;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {
	
	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String resourceName, Object identifier) {
		super(HttpStatus.NOT_FOUND, 
				"Recurso Não Encontrado",
                String.format("'%s' com identificador '%s' não foi encontrado.", 
                		resourceName, identifier)
                );
	}

}
