package com.bndesigner.dto.request.cupom;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.bndesigner.domain.enums.cupom.StatusCupom;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CupomUpdateRequest(
		
		@NotBlank(message = "Código é obrigatório")
        @Size(max = 50)
        String codigo,
        
        @NotNull(message = "Desconto é obrigatório")
		@DecimalMin(value = "0.01")
		@DecimalMax(value = "100.00")
        BigDecimal descontoPercentual,
        
        @NotNull(message = "Data de validade é obrigatória")
		LocalDate dataValidade,
		
		@NotNull
		StatusCupom status
		
		) {}
