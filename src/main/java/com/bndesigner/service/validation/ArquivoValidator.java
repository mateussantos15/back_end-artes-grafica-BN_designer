package com.bndesigner.service.validation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.bndesigner.domain.entity.arquivo.Arquivo;
import com.bndesigner.exceptions.BusinessException;
import com.bndesigner.exceptions.ResourceNotFoundException;
import com.bndesigner.repository.arquivo.ArquivoRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArquivoValidator {
	
	private final ArquivoRepository arquivoRepository;
	
	/**
     * Retorna o Arquivo correspondente ao id informado, validando se está ativo.
     * Retorna {@code null} quando o id é {@code null}, o que permite tanto
     * omitir o arquivo na criação quanto removê-lo explicitamente na atualização.
     */
    public Arquivo resolverArquivo(Long arquivoId) {
    	
        if (arquivoId == null) {
            return null;
        }
 
        Arquivo arquivo = arquivoRepository.findById(arquivoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Arquivo",
                        arquivoId
                ));
 
        if (!Boolean.TRUE.equals(arquivo.getAtivo())) {
            throw new BusinessException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Regra de negócio violada",
                    "Arquivo com id %d está inativo e não pode ser associado.".formatted(arquivoId)
            );
        }
 
        return arquivo;
    }
}
