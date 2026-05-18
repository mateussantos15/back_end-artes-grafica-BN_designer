package com.bndesigner.mapper.cupom;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.bndesigner.domain.entity.cupom.Cupom;
import com.bndesigner.dto.request.copum.CupomCreateRequest;
import com.bndesigner.dto.request.copum.CupomUpdateRequest;
import com.bndesigner.dto.response.cupom.CupomResponse;

@Mapper(componentModel = "spring")
public interface CupomMapper {
	
	Cupom toEntity(CupomCreateRequest createRequest);
	
	CupomResponse toResponse(Cupom entity);
	
	@BeanMapping(nullValuePropertyMappingStrategy = 
			NullValuePropertyMappingStrategy.IGNORE)
	void updateEntityFromRequest(
			CupomUpdateRequest updateRequest,
			@MappingTarget Cupom entity);

}
