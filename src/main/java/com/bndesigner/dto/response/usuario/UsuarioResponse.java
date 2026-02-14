package com.bndesigner.dto.response.usuario;

public record UsuarioResponse (
	
	Long idUsuario,
	String nome,
	String sobrenome,
	String email
) {}
