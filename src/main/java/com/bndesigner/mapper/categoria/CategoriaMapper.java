package com.bndesigner.mapper.categoria;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.bndesigner.domain.entity.categoria.Categoria;
import com.bndesigner.dto.request.categoria.CategoriaCreateRequest;
import com.bndesigner.dto.request.categoria.CategoriaUpdateRequest;
import com.bndesigner.dto.response.categoria.CategoriaResponse;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {
	
	@Mapping(target = "idCategoria", ignore = true)
	Categoria toEntity(CategoriaCreateRequest createRequest);
	
	CategoriaResponse toResponse(Categoria entity);
	
	void updateEntityFromRequest(CategoriaUpdateRequest updateRequest,
			@MappingTarget Categoria entity);

}
