package com.bndesigner.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.bndesigner.domain.entity.avaliacao.Avaliacao;
import com.bndesigner.domain.entity.produto.Produto;
import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.dto.request.avaliacao.AvaliacaoCreateRequest;
import com.bndesigner.dto.request.avaliacao.AvaliacaoUpdateRequest;
import com.bndesigner.dto.response.avaliacao.AvaliacaoResponse;
import com.bndesigner.mapper.avaliacao.AvaliacaoMapper;

class AvaliacaoMapperTest {

    private final AvaliacaoMapper mapper =
            Mappers.getMapper(AvaliacaoMapper.class);

    @Test
    void deveMapearCreateRequestParaEntity() {

        Usuario usuario = Usuario.builder()
                .idUsuario(1L)
                .build();

        Produto produto = Produto.builder()
                .idProduto(10L)
                .build();

        AvaliacaoCreateRequest request =
                new AvaliacaoCreateRequest(
                        10L,
                        5,
                        "Excelente produto!");

        Avaliacao avaliacao =
                mapper.toEntity(request, usuario, produto);

        assertNotNull(avaliacao);
        assertEquals(5, avaliacao.getNota());
        assertEquals(
                "Excelente produto!",
                avaliacao.getComentario());
        assertEquals(
                usuario,
                avaliacao.getUsuario());
        assertEquals(
                produto,
                avaliacao.getProduto());
    }

    @Test
    void deveMapearEntityParaResponse() {

        Usuario usuario = Usuario.builder()
                .idUsuario(1L)
                .build();

        Produto produto = Produto.builder()
                .idProduto(10L)
                .build();

        LocalDateTime data =
                LocalDateTime.of(2026, 10, 13, 10, 0);

        Avaliacao avaliacao = Avaliacao.builder()
                .idAvaliacao(5L)
                .usuario(usuario)
                .produto(produto)
                .nota(4)
                .comentario("Muito bom.")
                .dataAvaliacao(data)
                .build();

        AvaliacaoResponse response =
                mapper.toResponse(avaliacao);

        assertNotNull(response);
        assertEquals(5L, response.idAvaliacao());
        assertEquals(10L, response.produtoId());
        assertEquals(1L, response.usuarioId());
        assertEquals(4, response.nota());
        assertEquals("Muito bom.", response.comentario());
        assertEquals(data, response.dataAvaliacao());
    }

    @Test
    void deveAtualizarSomenteCamposPermitidos() {

        Usuario usuario = Usuario.builder()
                .idUsuario(1L)
                .build();

        Produto produto = Produto.builder()
                .idProduto(10L)
                .build();

        LocalDateTime data =
                LocalDateTime.of(2026, 10, 13, 10, 0);

        Avaliacao avaliacao = Avaliacao.builder()
                .idAvaliacao(5L)
                .usuario(usuario)
                .produto(produto)
                .nota(3)
                .comentario("Comentário antigo.")
                .dataAvaliacao(data)
                .build();

        AvaliacaoUpdateRequest request =
                new AvaliacaoUpdateRequest(
                        5,
                        "Comentário atualizado.");

        mapper.updateEntity(request, avaliacao);

        assertEquals(5, avaliacao.getNota());
        assertEquals(
                "Comentário atualizado.",
                avaliacao.getComentario());

        assertEquals(5L, avaliacao.getIdAvaliacao());
        assertEquals(usuario, avaliacao.getUsuario());
        assertEquals(produto, avaliacao.getProduto());
        assertEquals(data, avaliacao.getDataAvaliacao());
    }
}