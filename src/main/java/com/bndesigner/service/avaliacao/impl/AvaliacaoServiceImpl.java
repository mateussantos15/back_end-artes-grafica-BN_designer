package com.bndesigner.service.avaliacao.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bndesigner.domain.entity.avaliacao.Avaliacao;
import com.bndesigner.domain.entity.produto.Produto;
import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.domain.resolver.AvaliacaoResolver;
import com.bndesigner.domain.resolver.ProdutoResolver;
import com.bndesigner.domain.resolver.UsuarioResolver;
import com.bndesigner.domain.validation.AvaliacaoValidator;
import com.bndesigner.dto.request.avaliacao.AvaliacaoCreateRequest;
import com.bndesigner.dto.request.avaliacao.AvaliacaoUpdateRequest;
import com.bndesigner.dto.response.avaliacao.AvaliacaoResponse;
import com.bndesigner.mapper.avaliacao.AvaliacaoMapper;
import com.bndesigner.repository.avaliacao.AvaliacaoRepository;
import com.bndesigner.service.avaliacao.AvaliacaoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AvaliacaoServiceImpl implements AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final AvaliacaoMapper avaliacaoMapper;
    private final AvaliacaoResolver avaliacaoResolver;
    private final UsuarioResolver usuarioResolver;
    private final ProdutoResolver produtoResolver;
    private final AvaliacaoValidator avaliacaoValidator;

    @Override
    public AvaliacaoResponse criar(
            Long usuarioId,
            AvaliacaoCreateRequest request) {

        Usuario usuario = usuarioResolver.buscarUsuario(usuarioId);

        Produto produto = produtoResolver.buscarProdutoDisponivel(
                request.produtoId());

        avaliacaoValidator.validarNovaAvaliacao(
                usuarioId,
                request.produtoId());

        Avaliacao avaliacao = avaliacaoMapper.toEntity(
                request,
                usuario,
                produto);

        Avaliacao salva = avaliacaoRepository.save(avaliacao);

        return avaliacaoMapper.toResponse(salva);
    }

    @Override
    @Transactional(readOnly = true)
    public AvaliacaoResponse buscarPorId(Long avaliacaoId) {

        Avaliacao avaliacao =
                avaliacaoResolver.buscarAvaliacao(avaliacaoId);

        return avaliacaoMapper.toResponse(avaliacao);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AvaliacaoResponse> listarPorProduto(
            Long produtoId,
            Pageable pageable) {

        produtoResolver.buscarProdutoDisponivel(produtoId);

        return avaliacaoRepository
                .findByProdutoIdProduto(produtoId, pageable)
                .map(avaliacaoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AvaliacaoResponse> listarPorUsuario(
            Long usuarioId,
            Pageable pageable) {

        usuarioResolver.buscarUsuario(usuarioId);

        return avaliacaoRepository
                .findByUsuarioIdUsuario(usuarioId, pageable)
                .map(avaliacaoMapper::toResponse);
    }

    @Override
    public AvaliacaoResponse atualizar(
            Long avaliacaoId,
            AvaliacaoUpdateRequest request) {

        Avaliacao avaliacao =
                avaliacaoResolver.buscarAvaliacao(avaliacaoId);

        avaliacaoMapper.updateEntity(request, avaliacao);

        Avaliacao atualizada =
                avaliacaoRepository.save(avaliacao);

        return avaliacaoMapper.toResponse(atualizada);
    }
}