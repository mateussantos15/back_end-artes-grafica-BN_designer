package com.bndesigner.dto.request.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		
		@NotBlank(message = "E-mail é obrigatório!")
		@Email(message = "Email inválido")
		String email,
		
		@NotBlank(message = "Senha é obrigatória")
		String senha
		
		) {
}
