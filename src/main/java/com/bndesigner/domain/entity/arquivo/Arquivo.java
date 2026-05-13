package com.bndesigner.domain.entity.arquivo;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@EntityListeners(AuditingEntityListener.class)
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
	
	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime dataUpload;
	
	@LastModifiedDate
	private LocalDateTime dataAtualizacao;
	
	@Column(nullable = false)
	private Boolean ativo;
	
	@PrePersist
	protected void onCreate() {
		if (dataUpload == null) dataUpload = LocalDateTime.now();
		if (ativo == null) ativo = true;
	}
}
