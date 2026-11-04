package com.bndesigner.controller.pagamento;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bndesigner.dto.request.pagamento.PagamentoCreateRequest;
import com.bndesigner.dto.response.pagamento.PagamentoResponse;
import com.bndesigner.config.PasswordConfig;
import com.bndesigner.config.SecurityConfig;
import com.bndesigner.domain.enums.pagamento.StatusPagamento;
import com.bndesigner.service.pagamento.PagamentoService;
import com.bndesigner.service.security.UsuarioDetailsService;

@WebMvcTest(PagamentoController.class)
@Import({SecurityConfig.class, PasswordConfig.class})
@WithMockUser
@DisplayName("Testes do controller de pagamento")
class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PagamentoService pagamentoService;
    
    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveCriarPagamento() throws Exception {

        PagamentoResponse response = new PagamentoResponse(
                1L,
                2L,
                10L,
                new BigDecimal("100.00"),
                StatusPagamento.PENDENTE,
                null,
                null,
                null
        );

        when(pagamentoService.criar(any(PagamentoCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/api/pagamentos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "pedidoId": 10,
                            "metodoPagamentoId": 2,
                            "valorPago": 100.00
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.idPagamento").value(1))
            .andExpect(jsonPath("$.pedidoId").value(10))
            .andExpect(jsonPath("$.metodoPagamentoId").value(2))
            .andExpect(jsonPath("$.valorPago").value(100.00))
            .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    void deveRetornarPagamentoPorId() throws Exception {

        PagamentoResponse response = new PagamentoResponse(
                1L,
                2L,
                10L,
                new BigDecimal("100.00"),
                StatusPagamento.PENDENTE,
                null,
                null,
                null
        );

        when(pagamentoService.buscarPorId(1L))
                .thenReturn(response);

        mockMvc.perform(
                get("/api/pagamentos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idPagamento").value(1))
            .andExpect(jsonPath("$.pedidoId").value(10))
            .andExpect(jsonPath("$.metodoPagamentoId").value(2))
            .andExpect(jsonPath("$.valorPago").value(100.00))
            .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    void deveRetornarPagamentoPorPedidoId() throws Exception {

        PagamentoResponse response = new PagamentoResponse(
                1L,
                2L,
                10L,
                new BigDecimal("100.00"),
                StatusPagamento.PENDENTE,
                null,
                null,
                null
        );

        when(pagamentoService.buscarPorPedidoId(10L))
                .thenReturn(response);

        mockMvc.perform(
                get("/api/pagamentos/pedido/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idPagamento").value(1))
            .andExpect(jsonPath("$.pedidoId").value(10))
            .andExpect(jsonPath("$.metodoPagamentoId").value(2))
            .andExpect(jsonPath("$.status").value("PENDENTE"));
    }

    @Test
    void deveRetornarBadRequestQuandoDadosObrigatoriosNaoForemInformados()
            throws Exception {

        mockMvc.perform(
                post("/api/pagamentos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "valorPago": 100.00
                        }
                        """))
            .andExpect(status().isBadRequest());
    }
}