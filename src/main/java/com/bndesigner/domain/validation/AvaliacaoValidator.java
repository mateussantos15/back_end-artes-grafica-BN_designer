package com.bndesigner.domain.validation;

import org.springframework.stereotype.Component;

import com.bndesigner.exceptions.custom.AvaliacaoJaExisteException;
import com.bndesigner.exceptions.custom.UsuarioNaoComprouProdutoException;
import com.bndesigner.repository.avaliacao.AvaliacaoRepository;
import com.bndesigner.repository.itempedido.ItemPedidoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AvaliacaoValidator {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public void validarNovaAvaliacao(Long usuarioId, Long produtoId) {

        validarAvaliacaoDuplicada(usuarioId, produtoId);
        validarCompraDoProduto(usuarioId, produtoId);
    }

    private void validarAvaliacaoDuplicada(Long usuarioId, Long produtoId) {

        boolean avaliacaoExiste = avaliacaoRepository
                .existsByUsuarioIdUsuarioAndProdutoIdProduto(
                        usuarioId,
                        produtoId);

        if (avaliacaoExiste) {
            throw new AvaliacaoJaExisteException (usuarioId, produtoId);
        }
    }

    private void validarCompraDoProduto(Long usuarioId, Long produtoId) {

        boolean produtoFoiComprado = itemPedidoRepository
                .existsByPedidoUsuarioIdUsuarioAndProdutoIdProduto(
                        usuarioId,
                        produtoId);

        if (!produtoFoiComprado) {
            throw new UsuarioNaoComprouProdutoException(
                    usuarioId,
                    produtoId);
        }
    }
}