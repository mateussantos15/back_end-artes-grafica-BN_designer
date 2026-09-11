package com.bndesigner.controller.pedido;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bndesigner.dto.request.pedido.PedidoCreatRequest;
import com.bndesigner.dto.response.pedido.PedidoResponse;
import com.bndesigner.service.pedido.CheckoutService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {
	
	private final CheckoutService checkoutService;
	
	@PostMapping("/checkout")
	@ResponseStatus(HttpStatus.CREATED)
	public PedidoResponse checkout(
			@Valid @RequestBody PedidoCreatRequest request ) {
		
		return checkoutService.criarPedido(request); 
		
	}
}
