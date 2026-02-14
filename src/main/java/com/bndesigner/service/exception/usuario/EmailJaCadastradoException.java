package com.bndesigner.service.exception.usuario;

public class EmailJaCadastradoException extends RuntimeException {
	
	public EmailJaCadastradoException(String email) {
		super("E-mail já Cadastrado: " + email);
	}
}
