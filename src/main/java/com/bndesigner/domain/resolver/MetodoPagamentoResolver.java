package com.bndesigner.domain.resolver;

import org.springframework.stereotype.Component;

import com.bndesigner.domain.entity.metodopagamento.MetodoPagamento;
import com.bndesigner.exceptions.custom.MetodoPagamentoInativoException;
import com.bndesigner.repository.metodopagamento.MetodoPagamentoRepository;
import com.bndesigner.util.EntityLookup;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MetodoPagamentoResolver {
	
	private final MetodoPagamentoRepository metodoPagamentoRepository;
	
	public MetodoPagamento buscarMetodoDisponivel(Long metodoPagamentoId) {
		
		MetodoPagamento metodoPagamento = EntityLookup.buscarOuLancar(
				metodoPagamentoRepository,
				metodoPagamentoId,
				"Método de pagamento");
		
		if (!Boolean.TRUE.equals(metodoPagamento.getAtivo())) {
			throw new MetodoPagamentoInativoException(metodoPagamentoId);
		}
		
		return metodoPagamento;
	}

}
