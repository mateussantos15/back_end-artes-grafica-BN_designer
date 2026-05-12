package com.bndesigner.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {
	
	public ResourceNotFoundException(String resourceName, Object identifier) {
		super(HttpStatus.NOT_FOUND, 
				"Recurso Não Encontrado",
                String.format("%s com identificador '%s' não foi encontrado.", 
                		resourceName, identifier)
                );
	}

}
