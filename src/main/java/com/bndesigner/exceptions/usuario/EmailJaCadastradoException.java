package com.bndesigner.exceptions.usuario;

public class EmailJaCadastradoException extends RuntimeException {
	
	public EmailJaCadastradoException(String email) {
		super("E-mail já Cadastrado: " + email);
	}
}
