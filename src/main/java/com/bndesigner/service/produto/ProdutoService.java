package com.bndesigner.service.produto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bndesigner.dto.request.produto.ProdutoCreateRequest;
import com.bndesigner.dto.request.produto.ProdutoUpdateRequest;
import com.bndesigner.dto.response.produto.ProdutoResponse;

public interface ProdutoService {

    ProdutoResponse criar(ProdutoCreateRequest createRequest);

    ProdutoResponse buscarPorId(Long id);

    Page<ProdutoResponse> listar(Pageable pageable);

    Page<ProdutoResponse> listarPorCategoria(Long idCategoria, Pageable pageable);

    ProdutoResponse atualizar(Long id, ProdutoUpdateRequest updateRequest);

    void deletar(Long id);
}
