package com.bndesigner.repository.pedido;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.bndesigner.config.JpaAuditingConfig;
import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.domain.enums.pedido.StatusPedido;

@DataJpaTest
@ActiveProfiles("test") // Ativa o arquivo application-test.properties se houver
@Import(JpaAuditingConfig.class)
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    private Pedido pedido1;
    private Pedido pedido2;

    @BeforeEach
    void setUp() {
        // Cenário comum para os testes: limpando o banco e criando dados de teste
        pedidoRepository.deleteAll();

        pedido1 = new Pedido();
        pedido1.setCpf("12345678901");
        pedido1.setEmailCliente("cliente.a@email.com");
        pedido1.setStatusPedido(StatusPedido.PENDENTE);
        pedido1.setValorTotal(new BigDecimal("70.00"));
        // Preencha outros campos obrigatórios do seu construtor/entidade Pedido

        pedido2 = new Pedido();
        pedido2.setCpf("98765432100");
        pedido2.setEmailCliente("cliente.b@email.com");
        pedido2.setStatusPedido(StatusPedido.PAGO);
        pedido2.setValorTotal(new BigDecimal("70.00"));

        pedidoRepository.saveAll(List.of(pedido1, pedido2));
    }

    @Test
    @DisplayName("Deve buscar pedidos por status retornando uma página vazia ou preenchida")
    void findByStatusSuccess() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Pedido> resultado = pedidoRepository.findByStatusPedido(StatusPedido.PENDENTE, pageable);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getCpf()).isEqualTo("12345678901");
    }

    @Test
    @DisplayName("Deve buscar pedidos por CPF com paginação")
    void findByCpfSuccess() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Pedido> resultado = pedidoRepository.findByCpf("98765432100", pageable);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getStatusPedido()).isEqualTo(StatusPedido.PAGO);
    }

    @Test
    @DisplayName("Deve retornar uma página vazia quando buscar por um CPF inexistente")
    void findByCpfEmpty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Pedido> resultado = pedidoRepository.findByCpf("00000000000", pageable);

        // Assert
        assertThat(resultado.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Deve buscar pedidos por e-mail do cliente com paginação")
    void findByEmailClienteSuccess() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Pedido> resultado = pedidoRepository.findByEmailCliente("cliente.a@email.com", pageable);

        // Assert
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getCpf()).isEqualTo("12345678901");
    }
}
