package com.bndesigner.controller.pagamento;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bndesigner.dto.request.pagamento.PagamentoCreateRequest;
import com.bndesigner.dto.response.pagamento.PagamentoResponse;
import com.bndesigner.service.pagamento.PagamentoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PagamentoResponse criar(
            @Valid @RequestBody PagamentoCreateRequest createRequest) {

        return pagamentoService.criar(createRequest);
    }

    @GetMapping("/{id}")
    public PagamentoResponse buscarPorId(
            @PathVariable Long id) {

        return pagamentoService.buscarPorId(id);
    }

    @GetMapping("/pedido/{pedidoId}")
    public PagamentoResponse buscarPorPedidoId(
            @PathVariable Long pedidoId) {

        return pagamentoService.buscarPorPedidoId(pedidoId);
    }
}
