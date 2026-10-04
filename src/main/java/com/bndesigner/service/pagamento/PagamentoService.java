package com.bndesigner.service.pagamento;

import com.bndesigner.dto.request.pagamento.PagamentoCreateRequest;
import com.bndesigner.dto.response.pagamento.PagamentoResponse;

public interface PagamentoService {

    PagamentoResponse criar(PagamentoCreateRequest request);

    PagamentoResponse buscarPorId(Long id);

    PagamentoResponse buscarPorPedidoId(Long pedidoId);
}
