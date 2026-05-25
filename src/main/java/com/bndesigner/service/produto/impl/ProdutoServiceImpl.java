package com.bndesigner.service.produto.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bndesigner.domain.entity.categoria.Categoria;
import com.bndesigner.domain.entity.produto.Produto;
import com.bndesigner.domain.resolver.ArquivoResolver;
import com.bndesigner.dto.request.produto.ProdutoCreateRequest;
import com.bndesigner.dto.request.produto.ProdutoUpdateRequest;
import com.bndesigner.dto.response.produto.ProdutoResponse;
import com.bndesigner.mapper.produto.ProdutoMapper;
import com.bndesigner.repository.categoria.CategoriaRepository;
import com.bndesigner.repository.produto.ProdutoRepository;
import com.bndesigner.service.produto.ProdutoService;
import com.bndesigner.util.EntityLookup;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProdutoServiceImpl implements ProdutoService {
	
	private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProdutoMapper produtoMapper;
    private final ArquivoResolver arquivoValidator;

	@Override
	public ProdutoResponse criar(ProdutoCreateRequest createRequest) {
		Categoria categoria = EntityLookup.buscarOuLancar(
				categoriaRepository, createRequest.categoriaId(), "Categoria");
		
		Produto produto = produtoMapper.toEntity(createRequest);
		produto.setCategoria(categoria);
		produto.setArquivo(arquivoValidator.buscarArquivoAtivo(createRequest.arquivoId()));
		
		return produtoMapper.toResponse(produtoRepository.save(produto));
	}

	@Override
	@Transactional(readOnly = true)
	public ProdutoResponse buscarPorId(Long id) {
		
		return produtoMapper.toResponse(EntityLookup.buscarOuLancar(
				produtoRepository, id, "Produto"));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProdutoResponse> listar(Pageable pageable) {
        return produtoRepository.findAll(pageable)
                .map(produtoMapper::toResponse);

	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProdutoResponse> listarPorCategoria(Long idCategoria, Pageable pageable) {
        
		EntityLookup.buscarOuLancar(
				categoriaRepository, idCategoria, "Categoria");
		
        return produtoRepository.findByCategoria_IdCategoria(idCategoria, pageable)
                .map(produtoMapper::toResponse);

	}

	@Override
	public ProdutoResponse atualizar(Long id, ProdutoUpdateRequest updateRequest) {
		
		Produto produto = EntityLookup.buscarOuLancar(
				produtoRepository, id, "Produto");
		
        Categoria categoria = EntityLookup.buscarOuLancar(
				categoriaRepository, updateRequest.categoriaId(), "Categoria");
 
        produtoMapper.updateEntityFromRequest(updateRequest, produto);
        produto.setCategoria(categoria);
 
        // arquivoId nulo no request remove explicitamente o arquivo do produto
        produto.setArquivo(arquivoValidator.buscarArquivoAtivo(updateRequest.arquivoId()));
 
        return produtoMapper.toResponse(produtoRepository.save(produto));

	}

	@Override
	public void deletar(Long id) {
		produtoRepository.delete(EntityLookup.buscarOuLancar(
				produtoRepository, id, "Produto"));
		
	}
}
