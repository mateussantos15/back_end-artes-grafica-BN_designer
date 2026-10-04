package com.bndesigner.dto.response.pagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bndesigner.domain.enums.pagamento.StatusPagamento;

public record PagamentoResponse(
		
		Long idPagamento,
		Long metodoPagamentoId,
		Long pedidoId,
		BigDecimal valorPago,
		StatusPagamento status,
		String codigoTransacao,
		LocalDateTime dataPagamento,
		String comprovanteUrl		
		
		) {
}
