package com.bndesigner.domain.entity.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bndesigner.domain.validation.AvaliacaoValidator;
import com.bndesigner.exceptions.custom.AvaliacaoJaExisteException;
import com.bndesigner.exceptions.custom.UsuarioNaoComprouProdutoException;
import com.bndesigner.repository.avaliacao.AvaliacaoRepository;
import com.bndesigner.repository.itempedido.ItemPedidoRepository;

@ExtendWith(MockitoExtension.class)
class AvaliacaoValidatorTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @InjectMocks
    private AvaliacaoValidator avaliacaoValidator;

    @Test
    void devePermitirNovaAvaliacaoQuandoUsuarioComprouProduto() {

        Long usuarioId = 1L;
        Long produtoId = 10L;

        when(avaliacaoRepository
                .existsByUsuarioIdUsuarioAndProdutoIdProduto(
                        usuarioId,
                        produtoId))
                .thenReturn(false);

        when(itemPedidoRepository
                .existsByPedidoUsuarioIdUsuarioAndProdutoIdProduto(
                        usuarioId,
                        produtoId))
                .thenReturn(true);

        assertDoesNotThrow(() ->
                avaliacaoValidator.validarNovaAvaliacao(
                        usuarioId,
                        produtoId));

        verify(avaliacaoRepository)
                .existsByUsuarioIdUsuarioAndProdutoIdProduto(
                        usuarioId,
                        produtoId);

        verify(itemPedidoRepository)
                .existsByPedidoUsuarioIdUsuarioAndProdutoIdProduto(
                        usuarioId,
                        produtoId);
    }

    @Test
    void deveLancarExcecaoQuandoAvaliacaoJaExiste() {

        Long usuarioId = 1L;
        Long produtoId = 10L;

        when(avaliacaoRepository
                .existsByUsuarioIdUsuarioAndProdutoIdProduto(
                        usuarioId,
                        produtoId))
                .thenReturn(true);

        assertThrows(
                AvaliacaoJaExisteException.class,
                () -> avaliacaoValidator.validarNovaAvaliacao(
                        usuarioId,
                        produtoId));

        verify(avaliacaoRepository)
                .existsByUsuarioIdUsuarioAndProdutoIdProduto(
                        usuarioId,
                        produtoId);

        verify(itemPedidoRepository, never())
                .existsByPedidoUsuarioIdUsuarioAndProdutoIdProduto(
                        anyLong(),
                        anyLong());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoComprouProduto() {

        Long usuarioId = 1L;
        Long produtoId = 10L;

        when(avaliacaoRepository
                .existsByUsuarioIdUsuarioAndProdutoIdProduto(
                        usuarioId,
                        produtoId))
                .thenReturn(false);

        when(itemPedidoRepository
                .existsByPedidoUsuarioIdUsuarioAndProdutoIdProduto(
                        usuarioId,
                        produtoId))
                .thenReturn(false);

        assertThrows(
                UsuarioNaoComprouProdutoException.class,
                () -> avaliacaoValidator.validarNovaAvaliacao(
                        usuarioId,
                        produtoId));

        verify(avaliacaoRepository)
                .existsByUsuarioIdUsuarioAndProdutoIdProduto(
                        usuarioId,
                        produtoId);

        verify(itemPedidoRepository)
                .existsByPedidoUsuarioIdUsuarioAndProdutoIdProduto(
                        usuarioId,
                        produtoId);
    }
}