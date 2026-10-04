package com.bndesigner.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.bndesigner.domain.entity.metodopagamento.MetodoPagamento;
import com.bndesigner.domain.entity.pagamento.Pagamento;
import com.bndesigner.domain.enums.pagamento.StatusPagamento;
import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.dto.request.pagamento.PagamentoCreateRequest;
import com.bndesigner.dto.request.pagamento.PagamentoUpdateRequest;
import com.bndesigner.dto.response.pagamento.PagamentoResponse;
import com.bndesigner.mapper.pagamento.PagamentoMapper;

class PagamentoMapperTest {

    private PagamentoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PagamentoMapper.class);
    }

    @Test
    void deveConverterCreateRequestParaEntity() {

        PagamentoCreateRequest request = new PagamentoCreateRequest(
                10L,
                2L,
                new BigDecimal("150.00"));

        Pedido pedido = Pedido.builder()
                .idPedido(10L)
                .build();

        MetodoPagamento metodoPagamento = MetodoPagamento.builder()
                .idMetodoPagamento(2L)
                .nomeMetodoPagamento("PIX")
                .ativo(true)
                .build();

        Pagamento pagamento = mapper.toEntity(
                request,
                pedido,
                metodoPagamento);

        assertThat(pagamento).isNotNull();
        assertThat(pagamento.getPedido()).isSameAs(pedido);
        assertThat(pagamento.getMetodoPagamento())
                .isSameAs(metodoPagamento);
        assertThat(pagamento.getValorPago())
                .isEqualByComparingTo("150.00");

        assertThat(pagamento.getIdPagamento()).isNull();
        assertThat(pagamento.getStatus()).isNull();
        assertThat(pagamento.getCodigoTransacao()).isNull();
        assertThat(pagamento.getDataPagamento()).isNull();
        assertThat(pagamento.getComprovanteUrl()).isNull();
    }

    @Test
    void deveConverterEntityParaResponse() {

        Pedido pedido = Pedido.builder()
                .idPedido(10L)
                .build();

        MetodoPagamento metodoPagamento = MetodoPagamento.builder()
                .idMetodoPagamento(2L)
                .nomeMetodoPagamento("PIX")
                .ativo(true)
                .build();

        LocalDateTime dataPagamento =
                LocalDateTime.of(2026, 9, 28, 10, 30);

        Pagamento pagamento = Pagamento.builder()
                .idPagamento(1L)
                .pedido(pedido)
                .metodoPagamento(metodoPagamento)
                .valorPago(new BigDecimal("150.00"))
                .status(StatusPagamento.APROVADO)
                .codigoTransacao("TX-123")
                .dataPagamento(dataPagamento)
                .comprovanteUrl("https://exemplo.com/comprovante")
                .build();

        PagamentoResponse response =
                mapper.toResponse(pagamento);

        assertThat(response.idPagamento())
                .isEqualTo(1L);
        assertThat(response.pedidoId())
                .isEqualTo(10L);
        assertThat(response.metodoPagamentoId())
                .isEqualTo(2L);
        assertThat(response.valorPago())
                .isEqualByComparingTo("150.00");
        assertThat(response.status())
                .isEqualTo(StatusPagamento.APROVADO);
        assertThat(response.codigoTransacao())
                .isEqualTo("TX-123");
        assertThat(response.dataPagamento())
                .isEqualTo(dataPagamento);
        assertThat(response.comprovanteUrl())
                .isEqualTo("https://exemplo.com/comprovante");
    }

    @Test
    void deveAtualizarApenasCamposPermitidos() {

        Pagamento pagamento = Pagamento.builder()
                .idPagamento(1L)
                .valorPago(new BigDecimal("100.00"))
                .status(StatusPagamento.PENDENTE)
                .codigoTransacao("TX-123")
                .build();

        PagamentoUpdateRequest request =
                new PagamentoUpdateRequest(
                        new BigDecimal("150.00"));

        mapper.updateEntity(request, pagamento);

        assertThat(pagamento.getValorPago())
                .isEqualByComparingTo("150.00");

        assertThat(pagamento.getIdPagamento())
                .isEqualTo(1L);

        assertThat(pagamento.getStatus())
                .isEqualTo(StatusPagamento.PENDENTE);

        assertThat(pagamento.getCodigoTransacao())
                .isEqualTo("TX-123");
    }
}
