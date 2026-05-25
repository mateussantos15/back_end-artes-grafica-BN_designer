package com.bndesigner.exceptions.custom;

import org.springframework.http.HttpStatus;

/**
 * Exceção de negócio lançada quando é feita uma tentativa de salvar ou atualizar 
 * um recurso que possui valores duplicados para campos que deveriam ser únicos.
 * <p>
 * Esta exceção herda de {@link BusinessException} e define automaticamente o status 
 * HTTP para {@link HttpStatus#CONFLICT} (409 Conflict).
 * </p>
 *
 * @author bndesigner
 * @since 1.0.0
 */
public class DuplicateResourceException extends BusinessException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constrói uma nova exceção informando qual recurso e campo geraram a duplicidade.
	 *
	 * @param resourceName Nome da entidade/recurso que gerou o conflito (ex: "Usuario").
	 * @param nameVariable Nome do atributo ou variável que está duplicado (ex: "email").
	 * @param identifier O valor propriamente dito que já existe no sistema (ex: "joao@email.com").
	 */
	public DuplicateResourceException(String resourceName, String nameVariable, 
			Object identifier) {
		
		super(HttpStatus.CONFLICT, 
				String.format("'%s' duplicado", nameVariable),
				String.format("Já existe um '%s' com esse '%s': '%s'", 
						resourceName, nameVariable, identifier)
				);		
	}
	
}