package com.bndesigner.dto.request.produto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProdutoUpdateRequest(

		@NotBlank(message = "Título é obrigatório") 
		@Size(max = 250) String titulo,

		String descricao,

		@NotNull(message = "Preço é obrigatório") 
		@DecimalMin(value = "0.01") BigDecimal preco,

		@NotNull(message = "Categoria é obrigatória") 
		
		Long categoriaId,

		Long arquivoId
		
		) {}
