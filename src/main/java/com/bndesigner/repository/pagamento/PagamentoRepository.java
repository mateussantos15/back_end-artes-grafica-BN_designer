package com.bndesigner.repository.pagamento;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bndesigner.domain.entity.pagamento.Pagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
	
	Optional<Pagamento> findByPedidoIdPedido(Long idPagamento);
	
	boolean existsByPedidoIdPedido(Long idPagamento);
	
	Optional<Pagamento> findByCodigoTransacao(String idPedido);

}
