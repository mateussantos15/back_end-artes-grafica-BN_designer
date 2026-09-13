package com.bndesigner.domain.entity.metodopagamento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metodo_pagamento")
public class MetodoPagamento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idMetodoPagamento;
	
	@Column(nullable = false, length = 50)
	private String nomeMetodoPagamento;
	
	@Column(length = 100)
	private String descricaoMetodoPagamento;
	
	@Column(nullable = false)
	private Boolean ativo;	

}
