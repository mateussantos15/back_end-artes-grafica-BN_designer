package com.bndesigner.repository.pagamento;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.bndesigner.config.JpaAuditingConfig;
import com.bndesigner.domain.entity.metodopagamento.MetodoPagamento;
import com.bndesigner.domain.entity.pagamento.Pagamento;
import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.domain.enums.pagamento.StatusPagamento;
import com.bndesigner.domain.enums.pedido.StatusPedido;
import com.bndesigner.repository.metodopagamento.MetodoPagamentoRepository;
import com.bndesigner.repository.pedido.PedidoRepository;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class PagamentoRepositoryTest {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private MetodoPagamentoRepository metodoPagamentoRepository;

    @Nested
    class FindByPedidoIdPedido {

        @Test
        void deveEncontrarPagamentoPeloIdDoPedido() {
            Pedido pedido = criarPedido();
            MetodoPagamento metodo = criarMetodoPagamento();

            Pagamento pagamento = criarPagamento(pedido, metodo);

            Optional<Pagamento> resultado =
                    pagamentoRepository.findByPedidoIdPedido(pedido.getIdPedido());

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getIdPagamento())
                    .isEqualTo(pagamento.getIdPagamento());
        }

        @Test
        void deveRetornarVazioQuandoPedidoNaoPossuiPagamento() {
            Pedido pedido = criarPedido();

            Optional<Pagamento> resultado =
                    pagamentoRepository.findByPedidoIdPedido(pedido.getIdPedido());

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    class ExistsByPedidoIdPedido {

        @Test
        void deveRetornarTrueQuandoPedidoPossuiPagamento() {
            Pedido pedido = criarPedido();
            MetodoPagamento metodo = criarMetodoPagamento();

            criarPagamento(pedido, metodo);

            boolean resultado =
                    pagamentoRepository.existsByPedidoIdPedido(pedido.getIdPedido());

            assertThat(resultado).isTrue();
        }

        @Test
        void deveRetornarFalseQuandoPedidoNaoPossuiPagamento() {
            Pedido pedido = criarPedido();

            boolean resultado =
                    pagamentoRepository.existsByPedidoIdPedido(pedido.getIdPedido());

            assertThat(resultado).isFalse();
        }
    }

    @Nested
    class FindByCodigoTransacao {

        @Test
        void deveEncontrarPagamentoPeloCodigoDaTransacao() {
            Pedido pedido = criarPedido();
            MetodoPagamento metodo = criarMetodoPagamento();

            Pagamento pagamento = Pagamento.builder()
                    .pedido(pedido)
                    .metodoPagamento(metodo)
                    .valorPago(new BigDecimal("100.00"))
                    .status(StatusPagamento.APROVADO)
                    .codigoTransacao("TX-123456")
                    .build();

            pagamento = pagamentoRepository.save(pagamento);

            Optional<Pagamento> resultado =
                    pagamentoRepository.findByCodigoTransacao("TX-123456");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getIdPagamento())
                    .isEqualTo(pagamento.getIdPagamento());
        }

        @Test
        void deveRetornarVazioQuandoCodigoNaoExiste() {
            Optional<Pagamento> resultado =
                    pagamentoRepository.findByCodigoTransacao("CODIGO-INEXISTENTE");

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    class CrudBasico {

        @Test
        void deveSalvarEBuscarPagamento() {
            Pedido pedido = criarPedido();
            MetodoPagamento metodo = criarMetodoPagamento();

            Pagamento pagamento = criarPagamento(pedido, metodo);

            Optional<Pagamento> resultado =
                    pagamentoRepository.findById(pagamento.getIdPagamento());

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getValorPago())
                    .isEqualByComparingTo("100.00");
            assertThat(resultado.get().getStatus())
                    .isEqualTo(StatusPagamento.PENDENTE);
        }
    }

    private Pedido criarPedido() {
        Pedido pedido = Pedido.builder()
                .statusPedido(StatusPedido.PENDENTE)
                .valorTotal(new BigDecimal("100.00"))
                .emailCliente("cliente@email.com")
                .cpf("12345678901")
                .build();

        return pedidoRepository.save(pedido);
    }

    private MetodoPagamento criarMetodoPagamento() {
        MetodoPagamento metodo = MetodoPagamento.builder()
                .nomeMetodoPagamento("PIX")
                .descricaoMetodoPagamento("Pagamento via PIX")
                .ativo(true)
                .build();

        return metodoPagamentoRepository.save(metodo);
    }

    private Pagamento criarPagamento(
            Pedido pedido,
            MetodoPagamento metodo) {

        Pagamento pagamento = Pagamento.builder()
                .pedido(pedido)
                .metodoPagamento(metodo)
                .valorPago(new BigDecimal("100.00"))
                .status(StatusPagamento.PENDENTE)
                .build();

        return pagamentoRepository.save(pagamento);
    }
}