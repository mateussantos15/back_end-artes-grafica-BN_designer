package com.bndesigner.domain.resolver;

import org.springframework.stereotype.Component;

import com.bndesigner.domain.entity.pagamento.Pagamento;
import com.bndesigner.repository.pagamento.PagamentoRepository;
import com.bndesigner.util.EntityLookup;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PagamentoResolver {

    private final PagamentoRepository pagamentoRepository;

    public Pagamento buscarPagamento(Long pagamentoId) {
        return EntityLookup.buscarOuLancar(
                pagamentoRepository,
                pagamentoId,
                "Pagamento");
    }
}
