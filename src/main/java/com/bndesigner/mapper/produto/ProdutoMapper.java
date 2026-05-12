package com.bndesigner.mapper.produto;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.bndesigner.domain.entity.produto.Produto;
import com.bndesigner.dto.request.produto.ProdutoCreateRequest;
import com.bndesigner.dto.request.produto.ProdutoUpdateRequest;
import com.bndesigner.dto.response.produto.ProdutoResponse;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    Produto toEntity(ProdutoCreateRequest request);

    @Mapping(target = "categoriaId", source = "categoria.idCategoria")
    @Mapping(target = "categoriaNome", source = "categoria.nome")
    @Mapping(target = "arquivoId", source = "arquivo.idArquivo")
    ProdutoResponse toResponse(Produto entity);

    @BeanMapping(nullValuePropertyMappingStrategy =
            NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(
            ProdutoUpdateRequest request,
            @MappingTarget Produto entity
    );
}