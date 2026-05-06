package com.bndesigner.domain.entity.arquivo;

import java.time.LocalDateTime;

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

@Entity
@Data
@Table(name = "arquivo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Arquivo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idArquivo;
	
	@Column(nullable = false, length = 225)
	private String caminhoArquivo;
	
	@Column(name = "hash_arquivo", length = 100)
	private String hashArquivo;
	
	@Column(updatable = false)
	private LocalDateTime dataUpload;
	
	@Column(nullable = false)
	private Boolean ativo;
	
	@PrePersist
	protected void onCreate() {
		if (dataUpload == null) dataUpload = LocalDateTime.now();
		if (ativo == null) ativo = true;
	}

}
