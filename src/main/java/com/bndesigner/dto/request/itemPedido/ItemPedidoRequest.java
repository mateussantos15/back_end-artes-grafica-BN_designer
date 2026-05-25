package com.bndesigner.dto.request.itemPedido;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemPedidoRequest(
		
		@NotNull(message = "Produto é obrigatório") 
		Long produtoId,
		
		@NotNull(message = "Quantidade é obrigatória") 
		@Min(value = 1, message = "Quantidade deve ser maior que zero") 
		Integer quantidade
		
		) {}
