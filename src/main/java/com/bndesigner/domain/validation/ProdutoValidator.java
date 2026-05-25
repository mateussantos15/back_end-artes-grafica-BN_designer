package com.bndesigner.domain.validation;

import org.springframework.stereotype.Component;

import com.bndesigner.domain.entity.produto.Produto;
import com.bndesigner.domain.resolver.ArquivoResolver;
import com.bndesigner.exceptions.custom.ProdutoSemArquivoException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProdutoValidator {
	
	private final ArquivoResolver arquivoResolver;
	
	public void validarProdutoDisponivel(Produto produto) {
		
		if (produto.getArquivo() == null) {
			
			throw new ProdutoSemArquivoException(produto.getTitulo());
		}
		
		arquivoResolver.buscarArquivoAtivo(produto.getArquivo().getIdArquivo());		
	}

}
