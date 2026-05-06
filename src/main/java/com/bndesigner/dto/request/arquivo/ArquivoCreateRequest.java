package com.bndesigner.dto.request.arquivo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArquivoCreateRequest(
		
		@NotBlank(message = "O caminho do arquivo é obrigatório")
	    @Size(max = 255, message = "O caminho não pode ultrapassar 255 caracteres")
	    String caminhoArquivo,
	 
	    @Size(max = 100, message = "O hash não pode ultrapassar 100 caracteres")
	    String hashArquivo
	    
	    ) {}
