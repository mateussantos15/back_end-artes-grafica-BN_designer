package com.bndesigner.domain.validation;

import org.springframework.stereotype.Component;

import com.bndesigner.dto.request.itemPedido.ItemPedidoRequest;
import com.bndesigner.dto.request.pedido.PedidoCreatRequest;
import com.bndesigner.exceptions.custom.ItemComQuantidadeInvalidaException;
import com.bndesigner.exceptions.custom.PedidoSemItensException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PedidoValidator {
	
	public void validarPedido(PedidoCreatRequest creatRequest) {
		
		validarItens(creatRequest);
	}
	
	
	private void validarItens(PedidoCreatRequest creatRequest) {
		
		if(creatRequest.itens() == null || creatRequest.itens().isEmpty()) {
			
			throw new PedidoSemItensException();
		}
		
		creatRequest.itens().forEach(this::validarQuantidade);
	}
	
	
	private void validarQuantidade(ItemPedidoRequest item) {
		
		if (item.quantidade() == null || item.quantidade() <= 0) {
			
			throw new ItemComQuantidadeInvalidaException(item.produtoId());
		}
	}

}
