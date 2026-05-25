package com.bndesigner.domain.resolver;

import org.springframework.stereotype.Component;

import com.bndesigner.domain.entity.produto.Produto;
import com.bndesigner.domain.validation.ProdutoValidator;
import com.bndesigner.repository.produto.ProdutoRepository;
import com.bndesigner.util.EntityLookup;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProdutoResolver {
	
	private final ProdutoRepository produtoRepository;
	private final ProdutoValidator produtoValidator;
	
	public Produto buscarProdutoDisponivel(Long produtoId) {
		
		Produto produto = EntityLookup.buscarOuLancar(
				produtoRepository, produtoId, "Produto");
		
		produtoValidator.validarProdutoDisponivel(produto);
		
		return produto;
		
	}

}
