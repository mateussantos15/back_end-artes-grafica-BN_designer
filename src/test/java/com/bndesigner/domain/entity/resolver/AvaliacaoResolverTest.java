package com.bndesigner.domain.entity.resolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bndesigner.domain.entity.avaliacao.Avaliacao;
import com.bndesigner.domain.resolver.AvaliacaoResolver;
import com.bndesigner.repository.avaliacao.AvaliacaoRepository;

@ExtendWith(MockitoExtension.class)
class AvaliacaoResolverTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @InjectMocks
    private AvaliacaoResolver avaliacaoResolver;

    @Test
    void deveBuscarAvaliacaoPorId() {

        Avaliacao avaliacao = Avaliacao.builder()
                .idAvaliacao(1L)
                .nota(5)
                .build();

        when(avaliacaoRepository.findById(1L))
                .thenReturn(Optional.of(avaliacao));

        Avaliacao resultado =
                avaliacaoResolver.buscarAvaliacao(1L);

        assertSame(avaliacao, resultado);
        assertEquals(1L, resultado.getIdAvaliacao());

        verify(avaliacaoRepository)
                .findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoAvaliacaoNaoExistir() {

        when(avaliacaoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> avaliacaoResolver.buscarAvaliacao(1L));

        verify(avaliacaoRepository)
                .findById(1L);
    }
}