package com.bndesigner.domain.resolver;

import org.springframework.stereotype.Component;

import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.repository.pedido.PedidoRepository;
import com.bndesigner.util.EntityLookup;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PedidoResolver {

    private final PedidoRepository pedidoRepository;

    public Pedido buscarPedido(Long pedidoId) {
        return EntityLookup.buscarOuLancar(
                pedidoRepository,
                pedidoId,
                "Pedido");
    }
}