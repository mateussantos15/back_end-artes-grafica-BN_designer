package com.bndesigner.mapper.pedido;

import org.mapstruct.Mapper;

import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.dto.request.pedido.PedidoCreatRequest;
import com.bndesigner.dto.response.pedido.PedidoResponse;

@Mapper(componentModel = "spring")
public interface PedidoMapper {
	
	Pedido toEntity(PedidoCreatRequest creatRequest);
	
	PedidoResponse toResponse(Pedido entity);

}
