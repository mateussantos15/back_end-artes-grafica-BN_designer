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

import com.bndesigner.domain.entity.pagamento.Pagamento;
import com.bndesigner.domain.resolver.PagamentoResolver;
import com.bndesigner.exceptions.custom.ResourceNotFoundException;
import com.bndesigner.repository.pagamento.PagamentoRepository;

@ExtendWith(MockitoExtension.class)
class PagamentoResolverTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    private PagamentoResolver pagamentoResolver;

    @BeforeEach
    void setUp() {
        pagamentoResolver = new PagamentoResolver(pagamentoRepository);
    }

    @Test
    void deveRetornarPagamentoQuandoEncontrado() {

        Long pagamentoId = 1L;

        Pagamento pagamento = Pagamento.builder()
                .idPagamento(pagamentoId)
                .build();

        when(pagamentoRepository.findById(pagamentoId))
                .thenReturn(Optional.of(pagamento));

        Pagamento resultado =
                pagamentoResolver.buscarPagamento(pagamentoId);

        assertThat(resultado).isSameAs(pagamento);

        verify(pagamentoRepository).findById(pagamentoId);
    }

    @Test
    void deveLancarExcecaoQuandoPagamentoNaoEncontrado() {

        Long pagamentoId = 999L;

        when(pagamentoRepository.findById(pagamentoId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                pagamentoResolver.buscarPagamento(pagamentoId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(pagamentoRepository).findById(pagamentoId);
    }
}
