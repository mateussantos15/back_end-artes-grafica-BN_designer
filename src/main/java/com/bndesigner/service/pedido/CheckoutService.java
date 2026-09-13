package com.bndesigner.service.pedido;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bndesigner.domain.entity.cupom.Cupom;
import com.bndesigner.domain.entity.itempedido.ItemPedido;
import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.domain.entity.produto.Produto;
import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.domain.enums.pedido.StatusPedido;
import com.bndesigner.domain.resolver.CupomResolver;
import com.bndesigner.domain.resolver.ProdutoResolver;
import com.bndesigner.domain.validation.PedidoValidator;
import com.bndesigner.dto.request.itempedido.ItemPedidoRequest;
import com.bndesigner.dto.request.pedido.PedidoCreatRequest;
import com.bndesigner.dto.response.pedido.PedidoResponse;
import com.bndesigner.mapper.pedido.PedidoMapper;
import com.bndesigner.repository.pedido.PedidoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckoutService {
	
	private final PedidoRepository pedidoRepository;
	private final ProdutoResolver produtoResolver;
	private final CupomResolver cupomResolver;
	private final PedidoValidator pedidoValidator;
	private final PedidoMapper pedidoMapper;
	
	public PedidoResponse criarPedido(PedidoCreatRequest creatRequest) {
		
		// validar pedido
		pedidoValidator.validarPedido(creatRequest);
		
		// Resolver Cupom
		Cupom cupom = cupomResolver.buscarCupomValido(creatRequest.cupomCodigo());		
		
		// Criar itens
		List<ItemPedido> itens = criarItensPedido(creatRequest.itens());
		
		// 4. calcula subtotal 
		
		BigDecimal subtotal = calcularSubtotal(itens); 
		
		// 5. aplica desconto 
		
		BigDecimal valorFinal = aplicarDesconto(subtotal, cupom); 
		
		// 6. monta pedido 
		
		Pedido pedido = montarPedido(creatRequest, cupom, itens, valorFinal ); 
		
		// 7. relaciona itens ao pedido 
		
		itens.forEach(item -> item.setPedido(pedido)); 
		
		// 8. persiste aggregate 
		
		Pedido salvo = pedidoRepository.save(pedido); 
		
		// 9. retorna response 
		
		return pedidoMapper.toResponse(salvo); 
		
	}
	
	// ───────────────────────────────────── 
	// ITENS 
	// ───────────────────────────────────── 
	
	private List<ItemPedido> criarItensPedido(List<ItemPedidoRequest> itensRequest) { 
		
		List<ItemPedido> itens = new ArrayList<>(); 
		
		for (ItemPedidoRequest itemRequest : itensRequest) { 
			
			Produto produto = produtoResolver.
					buscarProdutoDisponivel(itemRequest.produtoId()); 
			
			ItemPedido item = ItemPedido.builder() 
					.produto(produto) 
					.quantidade(itemRequest.quantidade()) 
					.valorUnitario(produto.getPreco()) 
					.build(); 
			
			itens.add(item); 
					
		} 
		
		return itens; 
		
	}
	
	// ───────────────────────────────────── 
	// CÁLCULO 
	// ───────────────────────────────────── 
	
	private BigDecimal calcularSubtotal(List<ItemPedido> itens) { 
		
		return itens.stream() 
				.map(item -> item.getValorUnitario() 
						.multiply(BigDecimal.valueOf(item.getQuantidade()))) 
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		} 
	
	private BigDecimal aplicarDesconto(BigDecimal subtotal, Cupom cupom) { 
		
		if (cupom == null) {
			
			return subtotal; 
			
		} 
		
		BigDecimal desconto = subtotal.multiply(cupom.getDescontoPercentual().
				divide(BigDecimal.valueOf(100))); 
		
		return subtotal.subtract(desconto); 
		
	}
	
	// ───────────────────────────────────── 
	// PEDIDO 
	// ───────────────────────────────────── 
	
	private Pedido montarPedido( 
			
			PedidoCreatRequest creatRequest, 
			Cupom cupom, List<ItemPedido> itens, 
			BigDecimal valorFinal) { 
		
		Pedido pedido = Pedido.builder().
				statusPedido(StatusPedido.PENDENTE).
				valorTotal(valorFinal).
				emailCliente(creatRequest.emailCliente()).
				cpf(creatRequest.cpf()).
				cupom(cupom).
				itens(itens).
				build(); 
		
		// usuário opcional 
		
		if (creatRequest.usuarioId() != null) { 
			Usuario usuario = new Usuario(); 
			usuario.setIdUsuario(creatRequest.usuarioId()); 
			pedido.setUsuario(usuario); 
			
		} 
		
		return pedido;		
	}
}
