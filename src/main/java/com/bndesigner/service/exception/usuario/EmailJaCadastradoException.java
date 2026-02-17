package com.bndesigner.service.exception.usuario;

public class EmailJaCadastradoException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public EmailJaCadastradoException(String email) {
		super("E-mail já Cadastrado: " + email);
	}
}
