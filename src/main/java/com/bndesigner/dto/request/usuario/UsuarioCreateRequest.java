package com.bndesigner.dto.request.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioCreateRequest (
	
	@NotBlank
	String nome,
	
	String sobrenome,
	
	@Email
	@NotBlank
	String emai,
	
	@NotBlank
	String senha

) {}
