package com.bndesigner.domain.entity.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.domain.resolver.PedidoResolver;
import com.bndesigner.exceptions.custom.ResourceNotFoundException;
import com.bndesigner.repository.pedido.PedidoRepository;

@ExtendWith(MockitoExtension.class)
class PedidoResolverTest {

    @Mock
    private PedidoRepository pedidoRepository;

    private PedidoResolver pedidoResolver;

    @BeforeEach
    void setUp() {
        pedidoResolver = new PedidoResolver(pedidoRepository);
    }

    @Test
    void deveRetornarPedidoQuandoEncontrado() {

        Long pedidoId = 1L;

        Pedido pedido = Pedido.builder()
                .idPedido(pedidoId)
                .build();

        when(pedidoRepository.findById(pedidoId))
                .thenReturn(Optional.of(pedido));

        Pedido resultado = pedidoResolver.buscarPedido(pedidoId);

        assertThat(resultado).isSameAs(pedido);

        verify(pedidoRepository).findById(pedidoId);
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoEncontrado() {

        Long pedidoId = 999L;

        when(pedidoRepository.findById(pedidoId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                pedidoResolver.buscarPedido(pedidoId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(pedidoRepository).findById(pedidoId);
    }
}