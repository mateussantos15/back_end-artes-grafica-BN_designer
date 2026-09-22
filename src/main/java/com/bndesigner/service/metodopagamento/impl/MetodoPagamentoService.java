package com.bndesigner.service.metodopagamento.impl;

import java.util.List;

import com.bndesigner.dto.request.metodopagamento.MetodoPagamentoCreateRequest;
import com.bndesigner.dto.response.metodopagamento.MetodoPagamentoResponse;
import com.bndesigner.dto.request.metodopagamento.MetodoPagamentoUpdateRequest;

public interface MetodoPagamentoService {

    MetodoPagamentoResponse criar(MetodoPagamentoCreateRequest request);

    MetodoPagamentoResponse buscarPorId(Long id);

    List<MetodoPagamentoResponse> listar();

    MetodoPagamentoResponse atualizar(
            Long id,
            MetodoPagamentoUpdateRequest request);

    void desativar(Long id);
}
