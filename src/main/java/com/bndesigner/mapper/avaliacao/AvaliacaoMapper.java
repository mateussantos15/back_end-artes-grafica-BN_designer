package com.bndesigner.mapper.avaliacao;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.bndesigner.domain.entity.avaliacao.Avaliacao;
import com.bndesigner.domain.entity.produto.Produto;
import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.dto.request.avaliacao.AvaliacaoCreateRequest;
import com.bndesigner.dto.request.avaliacao.AvaliacaoUpdateRequest;
import com.bndesigner.dto.response.avaliacao.AvaliacaoResponse;

@Mapper(componentModel = "spring")
public interface AvaliacaoMapper {

    @Mapping(target = "idAvaliacao", ignore = true)
    @Mapping(target = "dataAvaliacao", ignore = true)
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "produto", source = "produto")
    @Mapping(target = "nota", source = "request.nota")
    @Mapping(target = "comentario", source = "request.comentario")
    Avaliacao toEntity(
            AvaliacaoCreateRequest request,
            Usuario usuario,
            Produto produto);

    @Mapping(target = "produtoId", source = "produto.idProduto")
    @Mapping(target = "usuarioId", source = "usuario.idUsuario")
    AvaliacaoResponse toResponse(Avaliacao avaliacao);

    @Mapping(target = "idAvaliacao", ignore = true)
    @Mapping(target = "dataAvaliacao", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "produto", ignore = true)
    void updateEntity(
            AvaliacaoUpdateRequest request,
            @MappingTarget Avaliacao avaliacao);
}