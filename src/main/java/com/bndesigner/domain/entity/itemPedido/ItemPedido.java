package com.bndesigner.domain.entity.itemPedido;

import java.math.BigDecimal;

import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.domain.entity.produto.Produto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "item_pedido")
public class ItemPedido {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idItemPedido;
	
	@Column(nullable = false)
	private Integer quantidade;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal valorUnitario;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_produto", nullable = false)
	private Produto produto;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pedido", nullable = false)
	private Pedido pedido;

}
