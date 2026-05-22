package com.bndesigner.domain.entity.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.bndesigner.domain.entity.cupom.Cupom;
import com.bndesigner.domain.entity.itemPedido.ItemPedido;
import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.domain.enums.pedido.StatusPedido;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@EntityListeners(AuditingEntityListener.class)
@Table(name = "pedido")
public class Pedido {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idPedido;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusPedido statusPedido;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal valorTotal;
	
	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime dataPedido;
	
	@Column(length = 100)
	private String emailCliente;
	
	@Column(length = 11)
	private String cpf;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cupom")
	private Cupom cupom;
	
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<ItemPedido> itens = new ArrayList<>();

}
