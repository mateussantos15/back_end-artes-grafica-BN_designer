package com.bndesigner.dto.response.itemPedido;

import java.math.BigDecimal;

public record ItemPedidoResponse(
		
		Long idItemPedido,
		Long produtoId,
		String produtoTitulo,
		Integer quantitade,
		BigDecimal valorUnitario,
		BigDecimal subTotal
		
		) {}
