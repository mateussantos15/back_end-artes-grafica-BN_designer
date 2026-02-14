package com.bndesigner.service.exception.usuario;

public class UsuarioNaoEncontradoException extends RuntimeException {
	
	public UsuarioNaoEncontradoException(Long id) {
		super("Usuário não encontrado. Id: " + id);
	}
}
