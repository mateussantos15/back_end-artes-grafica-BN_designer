package com.bndesigner.service.produto.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bndesigner.domain.entity.arquivo.Arquivo;
import com.bndesigner.domain.entity.categoria.Categoria;
import com.bndesigner.domain.entity.produto.Produto;
import com.bndesigner.dto.request.produto.ProdutoCreateRequest;
import com.bndesigner.dto.request.produto.ProdutoUpdateRequest;
import com.bndesigner.dto.response.produto.ProdutoResponse;
import com.bndesigner.exceptions.BusinessException;
import com.bndesigner.exceptions.ResourceNotFoundException;
import com.bndesigner.mapper.produto.ProdutoMapper;
import com.bndesigner.repository.arquivo.ArquivoRepository;
import com.bndesigner.repository.categoria.CategoriaRepository;
import com.bndesigner.repository.produto.ProdutoRepository;
import com.bndesigner.service.produto.ProdutoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProdutoServiceImpl implements ProdutoService {
	
	private final ProdutoRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final ArquivoRepository arquivoRepository;
    private final ProdutoMapper mapper;

	@Override
	public ProdutoResponse criar(ProdutoCreateRequest createRequest) {
		Categoria categoria = buscarCategoriaPorId(createRequest.categoriaId());
		
		Produto produto = mapper.toEntity(createRequest);
		produto.setCategoria(categoria);
		produto.setArquivo(resolverArquivo(createRequest.arquivoId()));
		
		return mapper.toResponse(repository.save(produto));
	}

	@Override
	@Transactional(readOnly = true)
	public ProdutoResponse buscarPorId(Long id) {
		return mapper.toResponse(buscarProdutoPorId(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProdutoResponse> listar(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);

	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProdutoResponse> listarPorCategoria(Long idCategoria, Pageable pageable) {
        buscarCategoriaPorId(idCategoria);
        return repository.findByCategoria_IdCategoria(idCategoria, pageable)
                .map(mapper::toResponse);

	}

	@Override
	public ProdutoResponse atualizar(Long id, ProdutoUpdateRequest updateRequest) {
		Produto produto = buscarProdutoPorId(id);
        Categoria categoria = buscarCategoriaPorId(updateRequest.categoriaId());
 
        mapper.updateEntityFromRequest(updateRequest, produto);
        produto.setCategoria(categoria);
 
        // arquivoId nulo no request remove explicitamente o arquivo do produto
        produto.setArquivo(resolverArquivo(updateRequest.arquivoId()));
 
        return mapper.toResponse(repository.save(produto));

	}

	@Override
	public void deletar(Long id) {
		repository.delete(buscarProdutoPorId(id));
		
	}
	

    // ─────────────────────────────────────────────────────────────────────────
    // Métodos auxiliares privados
    // ─────────────────────────────────────────────────────────────────────────
	
    private Produto buscarProdutoPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produto",
                        id
                ));
    }
 
    private Categoria buscarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria",
                        id
                ));
    }
 
    /**
     * Retorna o Arquivo correspondente ao id informado, validando se está ativo.
     * Retorna {@code null} quando o id é {@code null}, o que permite tanto
     * omitir o arquivo na criação quanto removê-lo explicitamente na atualização.
     */
    private Arquivo resolverArquivo(Long arquivoId) {
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
