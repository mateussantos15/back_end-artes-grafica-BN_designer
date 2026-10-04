package com.bndesigner.service.pagamento.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bndesigner.domain.entity.metodopagamento.MetodoPagamento;
import com.bndesigner.domain.entity.pagamento.Pagamento;

import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.domain.enums.pagamento.StatusPagamento;
import com.bndesigner.domain.resolver.MetodoPagamentoResolver;
import com.bndesigner.domain.resolver.PagamentoResolver;
import com.bndesigner.domain.resolver.PedidoResolver;
import com.bndesigner.domain.validation.PagamentoValidator;
import com.bndesigner.dto.request.pagamento.PagamentoCreateRequest;
import com.bndesigner.dto.response.pagamento.PagamentoResponse;
import com.bndesigner.mapper.pagamento.PagamentoMapper;
import com.bndesigner.repository.pagamento.PagamentoRepository;
import com.bndesigner.service.pagamento.PagamentoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagamentoServiceImpl implements PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PagamentoMapper pagamentoMapper;
    private final PedidoResolver pedidoResolver;
    private final MetodoPagamentoResolver metodoPagamentoResolver;
    private final PagamentoResolver pagamentoResolver;
    private final PagamentoValidator pagamentoValidator;

    @Override
    @Transactional
    public PagamentoResponse criar(PagamentoCreateRequest request) {

        Pedido pedido = pedidoResolver.buscarPedido(request.pedidoId());

        pagamentoValidator.validarNovoPagamento(pedido);
        pagamentoValidator.validarValor(pedido, request.valorPago());

        MetodoPagamento metodoPagamento =
                metodoPagamentoResolver.buscarMetodoDisponivel(
                        request.metodoPagamentoId());

        Pagamento pagamento = pagamentoMapper.toEntity(
                request,
                pedido,
                metodoPagamento);

        pagamento.setStatus(StatusPagamento.PENDENTE);

        pagamento = pagamentoRepository.save(pagamento);

        return pagamentoMapper.toResponse(pagamento);
    }

    @Override
    @Transactional(readOnly = true)
    public PagamentoResponse buscarPorId(Long id) {

    	Pagamento pagamento = pagamentoResolver.buscarPagamento(id);
        
        return pagamentoMapper.toResponse(pagamento);
    }

    @Override
    @Transactional(readOnly = true)
    public PagamentoResponse buscarPorPedidoId(Long pedidoId) {

        Pagamento pagamento = pagamentoRepository
                .findByPedidoIdPedido(pedidoId)
                .orElseThrow();

        return pagamentoMapper.toResponse(pagamento);
    }
}