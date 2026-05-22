package com.bndesigner.repository.pedido;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.domain.enums.pedido.StatusPedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
	
	Page<Pedido> findByStatusPedido(StatusPedido status, Pageable pageable);
	
	Page<Pedido> findByCpf(String cpf, Pageable pageable);
	
	Page<Pedido> findByEmailCliente(String emailCliente, Pageable pageable);

}
