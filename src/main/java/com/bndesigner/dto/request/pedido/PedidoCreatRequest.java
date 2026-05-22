package com.bndesigner.dto.request.pedido;

import jakarta.validation.constraints.NotBlank;

public record PedidoCreatRequest(
		
		Long usuarioId,
		
		Long cupomId,
		
		@NotBlank
		String emailCliente,
		
		@NotBlank
		String cpf
		
		) {}
