package com.bndesigner.dto.request.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaCreateRequest (
		
		@NotBlank(message = "Nome da categoria é obrigatório")
		@Size(max = 45, message = "Nome deve ter no máximo 45 caracteres")
		String nome,
		
		@Size(max = 100, message = "Descrição deve ter no máximo 100 caracteres")
		String descricao
		
) {}
