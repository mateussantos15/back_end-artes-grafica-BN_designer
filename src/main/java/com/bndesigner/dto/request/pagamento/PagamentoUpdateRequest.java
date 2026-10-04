package com.bndesigner.dto.request.pagamento;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record PagamentoUpdateRequest(
		
		@NotNull(message = "O campo 'metodoPagamentoId' é obrigatório.")
		@DecimalMin(value = "0.01", inclusive = true, message = "O campo 'metodoPagamentoId' deve ser maior que zero.")
		BigDecimal valorPago
		
		) {

}
