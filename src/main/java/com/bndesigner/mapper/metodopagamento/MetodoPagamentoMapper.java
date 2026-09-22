package com.bndesigner.mapper.metodopagamento;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.bndesigner.domain.entity.metodopagamento.MetodoPagamento;
import com.bndesigner.dto.request.metodopagamento.MetodoPagamentoCreateRequest;
import com.bndesigner.dto.request.metodopagamento.MetodoPagamentoUpdateRequest;
import com.bndesigner.dto.response.metodopagamento.MetodoPagamentoResponse;

@Mapper(componentModel = "spring")
public interface MetodoPagamentoMapper {
	
	MetodoPagamento toEntity(MetodoPagamentoCreateRequest createRequest);
	
	MetodoPagamentoResponse toResponse(MetodoPagamento metodoPagamento);
	
	void updateEntityFromRequest(MetodoPagamentoUpdateRequest updateRequest, 
			@MappingTarget MetodoPagamento metodoPagamento);

}
