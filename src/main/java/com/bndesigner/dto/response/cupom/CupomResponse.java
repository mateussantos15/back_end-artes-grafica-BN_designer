package com.bndesigner.dto.response.cupom;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.bndesigner.domain.enums.cupom.StatusCupom;

public record CupomResponse(
		
		Long id,
		String codigo,
		BigDecimal descontoPercentual,
		LocalDate dataValidade,
		StatusCupom status
		
		) {}
