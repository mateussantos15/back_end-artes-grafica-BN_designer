package com.bndesigner.dto.request.pedido;

import java.util.List;

import com.bndesigner.dto.request.itemPedido.ItemPedidoRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record PedidoCreatRequest(
		
		Long usuarioId,
		
		Long cupomCodigo,
		
		@NotBlank(message = "Email é obrigatório")
		@Email(message = "Email inválido")
		String emailCliente,
		
		String cpf,
		
		@NotEmpty(message = "Pedido deve possuir itens")
		List<ItemPedidoRequest> itens
		
		) {}
