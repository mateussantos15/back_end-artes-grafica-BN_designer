package com.bndesigner.service.exception.usuario;

public class UsuarioNaoEncontradoException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public UsuarioNaoEncontradoException(Long id) {
		super("Usuário não encontrado. Id: " + id);
	}
}
