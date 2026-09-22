package com.bndesigner.dto.request.metodopagamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MetodoPagamentoUpdateRequest(
		
		@NotBlank(message = "O nome do método de pagamento é obrigatório.")
		@Size(max = 45, message = "O nome do método de pagamento não pode ter mais de 45 caracteres.")
		String nomeMetodoPagamento,
		
		@Size(max = 100, message = "A descrição do método de pagamento não pode ter mais de 100 caracteres.")
		String descricaoMetodoPagamento,
		
		Boolean ativo
		
		) {
}
