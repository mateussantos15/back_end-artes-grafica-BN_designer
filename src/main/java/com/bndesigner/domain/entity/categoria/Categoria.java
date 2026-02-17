package com.bndesigner.domain.entity.categoria;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categoria")

public class Categoria {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idCategoria;
	
	@Column(nullable = false)
	private String nome;
	
	private String descricao;
	
	@Column(nullable = false)
	private Boolean ativa;
	
	@Column(nullable = false, updatable = false)
	private OffsetDateTime dataCriacao;
	
	@PrePersist
	public void prePersist() {
		this.dataCriacao = OffsetDateTime.now();
		if (this.ativa == null) {
			this.ativa = true;
		}
	}

}
