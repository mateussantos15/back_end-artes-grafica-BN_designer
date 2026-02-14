package com.bndesigner.dto.request.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioUpdateRequest (
	
	@NotBlank
	String nome,
	
	String sobrenome,
	
	@NotBlank
	@Email
	String email
) {}
