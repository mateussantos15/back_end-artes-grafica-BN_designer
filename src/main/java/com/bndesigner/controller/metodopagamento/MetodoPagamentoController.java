package com.bndesigner.controller.metodopagamento;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bndesigner.dto.request.metodopagamento.MetodoPagamentoCreateRequest;
import com.bndesigner.dto.request.metodopagamento.MetodoPagamentoUpdateRequest;
import com.bndesigner.dto.response.metodopagamento.MetodoPagamentoResponse;
import com.bndesigner.service.metodopagamento.impl.MetodoPagamentoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/metodos-pagamento")
@RequiredArgsConstructor
public class MetodoPagamentoController {

    private final MetodoPagamentoService metodoPagamentoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MetodoPagamentoResponse criar(
            @Valid @RequestBody MetodoPagamentoCreateRequest createRequest) {

        return metodoPagamentoService.criar(createRequest);
    }

    @GetMapping("/{id}")
    public MetodoPagamentoResponse buscarPorId(
            @PathVariable Long id) {

        return metodoPagamentoService.buscarPorId(id);
    }

    @GetMapping
    public List<MetodoPagamentoResponse> listar() {

        return metodoPagamentoService.listar();
    }

    @PutMapping("/{id}")
    public MetodoPagamentoResponse atualizar(
            @PathVariable Long id,
            @RequestBody @Valid MetodoPagamentoUpdateRequest updateRequest) {

        return metodoPagamentoService.atualizar(id, updateRequest);
    }

    @PatchMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(
            @PathVariable Long id) {

        metodoPagamentoService.desativar(id);
    }
}