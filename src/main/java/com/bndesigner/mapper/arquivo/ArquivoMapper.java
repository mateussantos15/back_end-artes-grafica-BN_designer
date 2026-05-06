package com.bndesigner.mapper.arquivo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.bndesigner.domain.entity.arquivo.Arquivo;
import com.bndesigner.dto.request.arquivo.ArquivoCreateRequest;
import com.bndesigner.dto.request.arquivo.ArquivoUpdateRequest;
import com.bndesigner.dto.response.arquivo.ArquivoResponse;

@Mapper(componentModel = "spring")
public interface ArquivoMapper {
	
	@Mapping(target = "idArquivo", ignore = true)
	Arquivo toEntity(ArquivoCreateRequest creatRequest);
	
	ArquivoResponse toResponse(Arquivo entity);
	
	void updateEntityFromRequest(ArquivoUpdateRequest updateRequest,
			@MappingTarget Arquivo entity);
	
}
