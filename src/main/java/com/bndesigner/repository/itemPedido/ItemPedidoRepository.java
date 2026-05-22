package com.bndesigner.repository.itemPedido;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bndesigner.domain.entity.itemPedido.ItemPedido;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
	
	Page<ItemPedido> findByPedidoIdPedido(Long pedidoId, Pageable pageable);
	
	Page<ItemPedido> findByProdutoIdProduto(Long produtoId, Pageable pageable);

}
