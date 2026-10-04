package com.bndesigner.service.pagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bndesigner.domain.entity.metodopagamento.MetodoPagamento;
import com.bndesigner.domain.entity.pagamento.Pagamento;
import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.domain.enums.pagamento.StatusPagamento;
import com.bndesigner.domain.resolver.MetodoPagamentoResolver;
import com.bndesigner.domain.resolver.PagamentoResolver;
import com.bndesigner.domain.resolver.PedidoResolver;
import com.bndesigner.domain.validation.PagamentoValidator;
import com.bndesigner.dto.request.pagamento.PagamentoCreateRequest;
import com.bndesigner.dto.response.pagamento.PagamentoResponse;
import com.bndesigner.exceptions.custom.PagamentoJaExisteException;
import com.bndesigner.exceptions.custom.ValorPagamentoInvalidoException;
import com.bndesigner.mapper.pagamento.PagamentoMapper;
import com.bndesigner.repository.pagamento.PagamentoRepository;
import com.bndesigner.service.pagamento.impl.PagamentoServiceImpl;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private PagamentoMapper pagamentoMapper;

    @Mock
    private PedidoResolver pedidoResolver;

    @Mock
    private MetodoPagamentoResolver metodoPagamentoResolver;

    @Mock
    private PagamentoResolver pagamentoResolver;

    @Mock
    private PagamentoValidator pagamentoValidator;

    @InjectMocks
    private PagamentoServiceImpl pagamentoService;

    private Pedido pedido;
    private MetodoPagamento metodoPagamento;
    private Pagamento pagamento;
    private PagamentoResponse response;

    @BeforeEach
    void setUp() {

        pedido = Pedido.builder()
                .idPedido(1L)
                .valorTotal(new BigDecimal("100.00"))
                .build();

        metodoPagamento = MetodoPagamento.builder()
                .idMetodoPagamento(1L)
                .nomeMetodoPagamento("PIX")
                .ativo(true)
                .build();

        pagamento = Pagamento.builder()
                .idPagamento(1L)
                .pedido(pedido)
                .metodoPagamento(metodoPagamento)
                .valorPago(new BigDecimal("100.00"))
                .status(StatusPagamento.PENDENTE)
                .build();

        response = new PagamentoResponse(
                1L,
                1L,
                1L,
                new BigDecimal("100.00"),
                StatusPagamento.PENDENTE,
                null,
                null,
                null
        );
    }

    @Test
    void deveCriarPagamento() {

        PagamentoCreateRequest request =
                new PagamentoCreateRequest(
                        1L,
                        1L,
                        new BigDecimal("100.00"));

        when(pedidoResolver.buscarPedido(1L))
                .thenReturn(pedido);

        when(metodoPagamentoResolver.buscarMetodoDisponivel(1L))
                .thenReturn(metodoPagamento);

        when(pagamentoMapper.toEntity(
                request,
                pedido,
                metodoPagamento))
                .thenReturn(pagamento);

        when(pagamentoRepository.save(pagamento))
                .thenReturn(pagamento);

        when(pagamentoMapper.toResponse(pagamento))
                .thenReturn(response);

        PagamentoResponse resultado =
                pagamentoService.criar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.idPagamento());
        assertEquals(1L, resultado.pedidoId());
        assertEquals(1L, resultado.metodoPagamentoId());
        assertEquals(StatusPagamento.PENDENTE, resultado.status());
        assertEquals(
                new BigDecimal("100.00"),
                resultado.valorPago());

        verify(pedidoResolver).buscarPedido(1L);
        verify(pagamentoValidator).validarNovoPagamento(pedido);
        verify(pagamentoValidator)
                .validarValor(
                        pedido,
                        new BigDecimal("100.00"));
        verify(metodoPagamentoResolver)
                .buscarMetodoDisponivel(1L);
        verify(pagamentoRepository).save(pagamento);
    }

    @Test
    void deveBuscarPagamentoPorId() {

        when(pagamentoResolver.buscarPagamento(1L))
                .thenReturn(pagamento);

        when(pagamentoMapper.toResponse(pagamento))
                .thenReturn(response);

        PagamentoResponse resultado =
                pagamentoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.idPagamento());

        verify(pagamentoResolver)
                .buscarPagamento(1L);

        verify(pagamentoMapper)
                .toResponse(pagamento);
    }

    @Test
    void deveBuscarPagamentoPorPedidoId() {

        when(pagamentoRepository.findByPedidoIdPedido(1L))
                .thenReturn(Optional.of(pagamento));

        when(pagamentoMapper.toResponse(pagamento))
                .thenReturn(response);

        PagamentoResponse resultado =
                pagamentoService.buscarPorPedidoId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.idPagamento());
        assertEquals(1L, resultado.pedidoId());

        verify(pagamentoRepository)
                .findByPedidoIdPedido(1L);

        verify(pagamentoMapper)
                .toResponse(pagamento);
    }

    @Test
    void deveLancarExcecaoQuandoPagamentoNaoExisteParaPedido() {

        when(pagamentoRepository.findByPedidoIdPedido(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> pagamentoService.buscarPorPedidoId(1L));

        verify(pagamentoMapper, never())
                .toResponse(pagamento);
    }

    @Test
    void devePropagarExcecaoQuandoPedidoJaPossuiPagamento() {

        PagamentoCreateRequest request =
                new PagamentoCreateRequest(
                        1L,
                        1L,
                        new BigDecimal("100.00"));

        when(pedidoResolver.buscarPedido(1L))
                .thenReturn(pedido);

        org.mockito.Mockito.doThrow(
                new PagamentoJaExisteException(1L))
                .when(pagamentoValidator)
                .validarNovoPagamento(pedido);

        assertThrows(
                PagamentoJaExisteException.class,
                () -> pagamentoService.criar(request));

        verify(pagamentoRepository, never())
                .save(pagamento);

        verify(metodoPagamentoResolver, never())
                .buscarMetodoDisponivel(1L);
    }

    @Test
    void devePropagarExcecaoQuandoValorForInvalido() {

        PagamentoCreateRequest request =
                new PagamentoCreateRequest(
                        1L,
                        1L,
                        new BigDecimal("90.00"));

        when(pedidoResolver.buscarPedido(1L))
                .thenReturn(pedido);

        org.mockito.Mockito.doThrow(
                new ValorPagamentoInvalidoException(
                        1L,
                        pedido.getValorTotal(),
                        request.valorPago()))
                .when(pagamentoValidator)
                .validarValor(
                        pedido,
                        request.valorPago());

        assertThrows(
                ValorPagamentoInvalidoException.class,
                () -> pagamentoService.criar(request));

        verify(pagamentoRepository, never())
                .save(pagamento);

        verify(metodoPagamentoResolver, never())
                .buscarMetodoDisponivel(1L);
    }
}