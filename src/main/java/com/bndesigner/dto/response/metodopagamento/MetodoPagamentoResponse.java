package com.bndesigner.dto.response.metodopagamento;

public record MetodoPagamentoResponse(
		
		Long idMetodoPagamento,
		String nomeMetodoPagamento,
		String descricaoMetodoPagamento,
		Boolean ativo
		
		) {
}
