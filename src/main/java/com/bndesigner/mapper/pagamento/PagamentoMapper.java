package com.bndesigner.mapper.pagamento;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.bndesigner.domain.entity.metodopagamento.MetodoPagamento;
import com.bndesigner.domain.entity.pagamento.Pagamento;
import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.dto.request.pagamento.PagamentoCreateRequest;
import com.bndesigner.dto.request.pagamento.PagamentoUpdateRequest;
import com.bndesigner.dto.response.pagamento.PagamentoResponse;

@Mapper(componentModel = "spring")
public interface PagamentoMapper {
	
	@Mapping(target = "idPagamento", ignore = true)
	@Mapping(target = "pedido", source = "pedido")
	@Mapping(target = "metodoPagamento", source = "metodoPagamento")
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "codigoTransacao", ignore = true)
	@Mapping(target = "dataPagamento", ignore = true)
	@Mapping(target = "comprovanteUrl", ignore = true)
	@Mapping(target = "valorPago", source = "request.valorPago")
	Pagamento toEntity(
			PagamentoCreateRequest request,
			Pedido pedido,
			MetodoPagamento metodoPagamento);
	
	@Mapping(target = "pedidoId", source = "pedido.idPedido")
	@Mapping(target = "metodoPagamentoId", source = "metodoPagamento.idMetodoPagamento")
	PagamentoResponse toResponse(Pagamento pagamento);
	
	@Mapping(target = "idPagamento", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "metodoPagamento", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "codigoTransacao", ignore = true)
    @Mapping(target = "dataPagamento", ignore = true)
    @Mapping(target = "comprovanteUrl", ignore = true)
    void updateEntity(
            PagamentoUpdateRequest request,
            @MappingTarget Pagamento pagamento);

}
