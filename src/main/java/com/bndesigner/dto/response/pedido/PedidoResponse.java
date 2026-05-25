package com.bndesigner.dto.response.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.bndesigner.domain.enums.pedido.StatusPedido;
import com.bndesigner.dto.response.itemPedido.ItemPedidoResponse;

public record PedidoResponse(
		
		Long idPedido,
		StatusPedido statusPedido,
		BigDecimal valorTotal,
		LocalDateTime dataPedido,
		
		Long usuarioId,
		Long cupomId,
		
		String emailCliente,
		String cpf,
		
		List<ItemPedidoResponse> itens
		
		) {}
