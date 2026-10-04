package com.bndesigner.dto.request.pagamento;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record PagamentoCreateRequest(
		
		@NotNull(message = "O campo 'metodoPagamentoId' é obrigatório.")
		Long pedidoId,
		
		@NotNull(message = "O campo 'metodoPagamentoId' é obrigatório.")
		Long metodoPagamentoId,
		
		@NotNull(message = "O campo 'valorPago' é obrigatório.")
		@DecimalMin(value = "0.01", inclusive = false, message = "O campo 'valorPago' deve ser maior que zero.")
		BigDecimal valorPago
		
		) {

}
