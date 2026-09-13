package com.bndesigner.repository.metodopagamento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bndesigner.domain.entity.metodopagamento.MetodoPagamento;

public interface MetodoPagamentoRepository extends JpaRepository<MetodoPagamento, Long> {
	
	List<MetodoPagamento> findByAtivoTrue();

}
