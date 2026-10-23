package com.bndesigner.service.avaliacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bndesigner.dto.request.avaliacao.AvaliacaoCreateRequest;
import com.bndesigner.dto.request.avaliacao.AvaliacaoUpdateRequest;
import com.bndesigner.dto.response.avaliacao.AvaliacaoResponse;

public interface AvaliacaoService {

    AvaliacaoResponse criar(Long usuarioId, AvaliacaoCreateRequest request);

    AvaliacaoResponse buscarPorId(Long avaliacaoId);

    Page<AvaliacaoResponse> listarPorProduto(Long produtoId, Pageable pageable);

    Page<AvaliacaoResponse> listarPorUsuario(Long usuarioId, Pageable pageable);

    AvaliacaoResponse atualizar(
            Long avaliacaoId,
            AvaliacaoUpdateRequest request);
}