package com.bndesigner.dto.response.itempedido;

import java.math.BigDecimal;

public record ItemPedidoResponse(
		
		Long idItemPedido,
		Long produtoId,
		String produtoTitulo,
		Integer quantitade,
		BigDecimal valorUnitario,
		BigDecimal subTotal
		
		) {}
