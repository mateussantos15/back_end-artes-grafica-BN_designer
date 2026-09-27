package com.bndesigner.domain.entity.pagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.ManyToAny;

import com.bndesigner.domain.entity.metodopagamento.MetodoPagamento;
import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.domain.enums.pagamento.StatusPagamento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pagamento")
public class Pagamento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_pagamento")
	private Long idPagamento;
	
	@ManyToOne
	@JoinColumn(name = "id_metodo", nullable = false)
	private MetodoPagamento metodoPagamento;
	
	@Column(name = "valor_pago", nullable = false, precision = 10, scale = 2)
	private BigDecimal valorPago;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private StatusPagamento status;
	
	@Column(name = "codigo_transacao", length = 100)
	private String codigoTransacao;
	
	@Column(name = "data_pagamento")
	private LocalDateTime dataPagamento;
	
	@Column(name = "comprovante_url", length = 500)
	private String comprovanteUrl;
	
	@ManyToOne
	@JoinColumn(name = "id_pedido", nullable = false, unique = true)
	private Pedido pedido;
}
