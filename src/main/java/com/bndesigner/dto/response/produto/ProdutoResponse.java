package com.bndesigner.dto.response.produto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProdutoResponse(
		
		Long id,
        String titulo,
        String descricao,
        BigDecimal preco,
        LocalDateTime dataCadastro,

        Long categoriaId,
        String categoriaNome,

        Long arquivoId
        
		) {}
