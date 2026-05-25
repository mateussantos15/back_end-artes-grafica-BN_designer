package com.bndesigner.domain.resolver;

import org.springframework.stereotype.Component;

import com.bndesigner.domain.entity.arquivo.Arquivo;
import com.bndesigner.exceptions.custom.ArquivoInativoException;
import com.bndesigner.exceptions.custom.ResourceNotFoundException;
import com.bndesigner.repository.arquivo.ArquivoRepository;
import com.bndesigner.util.EntityLookup;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArquivoResolver {
	
	private final ArquivoRepository arquivoRepository;
	
	/**
     * Retorna o Arquivo correspondente ao id informado, validando se está ativo.
     * Retorna {@code null} quando o id é {@code null}, o que permite tanto
     * omitir o arquivo na criação quanto removê-lo explicitamente na atualização.
     */
    public Arquivo buscarArquivoAtivo(Long arquivoId) {
    	
        if (arquivoId == null) {
            return null;
        }
 
        Arquivo arquivo = EntityLookup.buscarOuLancar(
        		arquivoRepository, 
        		arquivoId, 
        		"Arquivo");
 
        if (!Boolean.TRUE.equals(arquivo.getAtivo())) {
            throw new ArquivoInativoException(arquivoId);
        }
 
        return arquivo;
    }
}
