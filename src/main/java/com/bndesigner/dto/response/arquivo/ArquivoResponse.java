package com.bndesigner.dto.response.arquivo;

import java.time.LocalDateTime;

public record ArquivoResponse(
		
		Long idArquivo,
		String caminhoArquivo,
		String hashArquivo,
		LocalDateTime dataUpload,
		Boolean ativo
		
		) {}