package com.bndesigner.domain.entity.cupom;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.bndesigner.domain.enums.cupom.StatusCupom;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cupom")
public class Cupom {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true, length = 50)
	private String codigo;
	
	@Column(precision = 5, scale = 2)
	private BigDecimal descontoPercentual;
	
	private LocalDate dataValidade;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusCupom status;
	
	
	public void atualizarStatus(Cupom cupom) {
		
		if(cupom.getDataValidade().isBefore(LocalDate.now())) {
			
			cupom.setStatus(StatusCupom.EXPIRADO);
			
		} else if (cupom.getStatus() == null) {
			
			cupom.setStatus(StatusCupom.ATIVO);
			
		}		
	}
}
