package com.bndesigner.service.avaliacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
import com.bndesigner.service.avaliacao.impl.AvaliacaoServiceImpl;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private AvaliacaoMapper avaliacaoMapper;

    @Mock
    private AvaliacaoResolver avaliacaoResolver;

    @Mock
    private UsuarioResolver usuarioResolver;

    @Mock
    private ProdutoResolver produtoResolver;

    @Mock
    private AvaliacaoValidator avaliacaoValidator;

    @InjectMocks
    private AvaliacaoServiceImpl avaliacaoService;

    @Test
    void deveCriarAvaliacao() {

        Long usuarioId = 1L;
        Long produtoId = 10L;

        AvaliacaoCreateRequest request =
                new AvaliacaoCreateRequest(
                        produtoId,
                        5,
                        "Excelente produto.");

        Usuario usuario = Usuario.builder()
                .idUsuario(usuarioId)
                .build();

        Produto produto = Produto.builder()
                .idProduto(produtoId)
                .build();

        Avaliacao avaliacao = Avaliacao.builder()
                .nota(5)
                .comentario("Excelente produto.")
                .usuario(usuario)
                .produto(produto)
                .build();

        Avaliacao salva = Avaliacao.builder()
                .idAvaliacao(100L)
                .nota(5)
                .comentario("Excelente produto.")
                .usuario(usuario)
                .produto(produto)
                .build();

        AvaliacaoResponse response =
                new AvaliacaoResponse(
                        100L,
                        produtoId,
                        usuarioId,
                        5,
                        "Excelente produto.",
                        null);

        when(usuarioResolver.buscarUsuario(usuarioId))
                .thenReturn(usuario);

        when(produtoResolver.buscarProdutoDisponivel(produtoId))
                .thenReturn(produto);

        when(avaliacaoMapper.toEntity(
                request,
                usuario,
                produto))
                .thenReturn(avaliacao);

        when(avaliacaoRepository.save(avaliacao))
                .thenReturn(salva);

        when(avaliacaoMapper.toResponse(salva))
                .thenReturn(response);

        AvaliacaoResponse resultado =
                avaliacaoService.criar(usuarioId, request);

        assertSame(response, resultado);

        verify(usuarioResolver)
                .buscarUsuario(usuarioId);

        verify(produtoResolver)
                .buscarProdutoDisponivel(produtoId);

        verify(avaliacaoValidator)
                .validarNovaAvaliacao(
                        usuarioId,
                        produtoId);

        verify(avaliacaoMapper)
                .toEntity(
                        request,
                        usuario,
                        produto);

        verify(avaliacaoRepository)
                .save(avaliacao);

        verify(avaliacaoMapper)
                .toResponse(salva);
    }
    
    @Test
    void deveBuscarAvaliacaoPorId() {

        Long avaliacaoId = 100L;

        Avaliacao avaliacao = Avaliacao.builder()
                .idAvaliacao(avaliacaoId)
                .nota(5)
                .comentario("Excelente produto.")
                .build();

        AvaliacaoResponse response =
                new AvaliacaoResponse(
                        avaliacaoId,
                        10L,
                        1L,
                        5,
                        "Excelente produto.",
                        null);

        when(avaliacaoResolver.buscarAvaliacao(avaliacaoId))
                .thenReturn(avaliacao);

        when(avaliacaoMapper.toResponse(avaliacao))
                .thenReturn(response);

        AvaliacaoResponse resultado =
                avaliacaoService.buscarPorId(avaliacaoId);

        assertSame(response, resultado);

        verify(avaliacaoResolver)
                .buscarAvaliacao(avaliacaoId);

        verify(avaliacaoMapper)
                .toResponse(avaliacao);
    }
    
    @Test
    void deveListarAvaliacoesPorProduto() {

        Long produtoId = 10L;

        Pageable pageable =
                PageRequest.of(0, 10);

        Avaliacao avaliacao = Avaliacao.builder()
                .idAvaliacao(100L)
                .nota(5)
                .comentario("Excelente produto.")
                .build();

        AvaliacaoResponse response =
                new AvaliacaoResponse(
                        100L,
                        produtoId,
                        1L,
                        5,
                        "Excelente produto.",
                        null);

        Page<Avaliacao> pagina =
                new PageImpl<>(List.of(avaliacao), pageable, 1);

        when(produtoResolver.buscarProdutoDisponivel(produtoId))
                .thenReturn(Produto.builder()
                        .idProduto(produtoId)
                        .build());

        when(avaliacaoRepository
                .findByProdutoIdProduto(produtoId, pageable))
                .thenReturn(pagina);

        when(avaliacaoMapper.toResponse(avaliacao))
                .thenReturn(response);

        Page<AvaliacaoResponse> resultado =
                avaliacaoService.listarPorProduto(
                        produtoId,
                        pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(response, resultado.getContent().get(0));

        verify(produtoResolver)
                .buscarProdutoDisponivel(produtoId);

        verify(avaliacaoRepository)
                .findByProdutoIdProduto(
                        produtoId,
                        pageable);

        verify(avaliacaoMapper)
                .toResponse(avaliacao);
    }
    
    @Test
    void deveListarAvaliacoesPorUsuario() {

        Long usuarioId = 1L;

        Pageable pageable =
                PageRequest.of(0, 10);

        Avaliacao avaliacao = Avaliacao.builder()
                .idAvaliacao(100L)
                .nota(5)
                .comentario("Excelente produto.")
                .build();

        AvaliacaoResponse response =
                new AvaliacaoResponse(
                        100L,
                        10L,
                        usuarioId,
                        5,
                        "Excelente produto.",
                        null);

        Page<Avaliacao> pagina =
                new PageImpl<>(List.of(avaliacao), pageable, 1);

        when(usuarioResolver.buscarUsuario(usuarioId))
                .thenReturn(Usuario.builder()
                        .idUsuario(usuarioId)
                        .build());

        when(avaliacaoRepository
                .findByUsuarioIdUsuario(usuarioId, pageable))
                .thenReturn(pagina);

        when(avaliacaoMapper.toResponse(avaliacao))
                .thenReturn(response);

        Page<AvaliacaoResponse> resultado =
                avaliacaoService.listarPorUsuario(
                        usuarioId,
                        pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(response, resultado.getContent().get(0));

        verify(usuarioResolver)
                .buscarUsuario(usuarioId);

        verify(avaliacaoRepository)
                .findByUsuarioIdUsuario(
                        usuarioId,
                        pageable);

        verify(avaliacaoMapper)
                .toResponse(avaliacao);
    }
    
    @Test
    void deveAtualizarAvaliacao() {

        Long avaliacaoId = 100L;

        AvaliacaoUpdateRequest request =
                new AvaliacaoUpdateRequest(
                        4,
                        "Produto muito bom.");

        Avaliacao avaliacao = Avaliacao.builder()
                .idAvaliacao(avaliacaoId)
                .nota(5)
                .comentario("Excelente produto.")
                .build();

        AvaliacaoResponse response =
                new AvaliacaoResponse(
                        avaliacaoId,
                        10L,
                        1L,
                        4,
                        "Produto muito bom.",
                        null);

        when(avaliacaoResolver.buscarAvaliacao(avaliacaoId))
                .thenReturn(avaliacao);

        when(avaliacaoRepository.save(avaliacao))
                .thenReturn(avaliacao);

        when(avaliacaoMapper.toResponse(avaliacao))
                .thenReturn(response);

        AvaliacaoResponse resultado =
                avaliacaoService.atualizar(
                        avaliacaoId,
                        request);

        assertSame(response, resultado);

        verify(avaliacaoResolver)
                .buscarAvaliacao(avaliacaoId);

        verify(avaliacaoMapper)
                .updateEntity(request, avaliacao);

        verify(avaliacaoRepository)
                .save(avaliacao);

        verify(avaliacaoMapper)
                .toResponse(avaliacao);
    }
}