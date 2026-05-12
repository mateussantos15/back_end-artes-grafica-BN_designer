package com.bndesigner.domain.entity.produto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bndesigner.domain.entity.arquivo.Arquivo;
import com.bndesigner.domain.entity.categoria.Categoria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
@Table(name = "produto")
public class Produto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "id_produto")
	private Long idProduto;
	
	@Column(nullable = false, length = 250)
	private String titulo;
	
	@Column(columnDefinition = "TEXT")
	private String descricao;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal preco;
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime dataCadastro;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria")
	private Categoria categoria;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_arquivo")
	private Arquivo arquivo;

	@PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }
}
