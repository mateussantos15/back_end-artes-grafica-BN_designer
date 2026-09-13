package com.bndesigner.controller.pedido;

import com.bndesigner.domain.enums.pedido.StatusPedido;
import com.bndesigner.dto.request.itempedido.ItemPedidoRequest;
import com.bndesigner.dto.request.pedido.PedidoCreatRequest;
import com.bndesigner.dto.response.itempedido.ItemPedidoResponse;
import com.bndesigner.dto.response.pedido.PedidoResponse;
import com.bndesigner.service.pedido.CheckoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
@DisplayName("PedidoController")
class PedidoControllerTest {

    private static final String URL = "/api/pedidos/checkout";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CheckoutService checkoutService;

    // ─── fixtures ───────────────────────────────────────────────────────────

    private ItemPedidoRequest itemRequest(Long produtoId, Integer quantidade) {
        return new ItemPedidoRequest(produtoId, quantidade);
    }

    private PedidoCreatRequest requestValido() {
        return new PedidoCreatRequest(
                null,
                null,
                "cliente@email.com",
                "12345678901",
                List.of(itemRequest(1L, 2))
        );
    }

    private PedidoResponse responsePadrao() {
        ItemPedidoResponse item = new ItemPedidoResponse(
                10L, 1L, "Produto A", 2,
                new BigDecimal("49.90"),
                new BigDecimal("99.80")
        );
        return new PedidoResponse(
                1L,
                StatusPedido.PENDENTE,
                new BigDecimal("99.80"),
                LocalDateTime.of(2025, 1, 15, 10, 30),
                null,
                null,
                "cliente@email.com",
                "12345678901",
                List.of(item)
        );
    }

    // ─── POST /checkout — sucesso ────────────────────────────────────────────

    @Nested
    @DisplayName("POST /checkout — sucesso")
    class Sucesso {

        @Test
        @DisplayName("deve retornar 201 e o PedidoResponse quando a requisição é válida")
        void deveRetornar201ComResponse() throws Exception {
            when(checkoutService.criarPedido(any())).thenReturn(responsePadrao());

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idPedido").value(1L))
                    .andExpect(jsonPath("$.statusPedido").value("PENDENTE"))
                    .andExpect(jsonPath("$.valorTotal").value(99.80))
                    .andExpect(jsonPath("$.emailCliente").value("cliente@email.com"))
                    .andExpect(jsonPath("$.cpf").value("12345678901"))
                    .andExpect(jsonPath("$.itens").isArray())
                    .andExpect(jsonPath("$.itens.length()").value(1));
        }

        @Test
        @DisplayName("deve retornar os dados do item no response")
        void deveRetornarDadosDoItem() throws Exception {
            when(checkoutService.criarPedido(any())).thenReturn(responsePadrao());

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.itens[0].produtoId").value(1L))
                    .andExpect(jsonPath("$.itens[0].produtoTitulo").value("Produto A"))
                    .andExpect(jsonPath("$.itens[0].valorUnitario").value(49.90))
                    .andExpect(jsonPath("$.itens[0].subTotal").value(99.80));
        }

        @Test
        @DisplayName("deve aceitar pedido com cupom informado")
        void deveAceitarPedidoComCupom() throws Exception {
            PedidoCreatRequest requestComCupom = new PedidoCreatRequest(
                    null, "PROMO10", "cliente@email.com", null,
                    List.of(itemRequest(1L, 1))
            );
            when(checkoutService.criarPedido(any())).thenReturn(responsePadrao());

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestComCupom)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("deve aceitar pedido com usuário autenticado")
        void deveAceitarPedidoComUsuario() throws Exception {
            PedidoCreatRequest requestComUsuario = new PedidoCreatRequest(
                    42L, null, "cliente@email.com", "12345678901",
                    List.of(itemRequest(1L, 3))
            );
            when(checkoutService.criarPedido(any())).thenReturn(responsePadrao());

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestComUsuario)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("deve delegar ao CheckoutService exatamente uma vez")
        void deveDelegarAoServiceUmaVez() throws Exception {
            when(checkoutService.criarPedido(any())).thenReturn(responsePadrao());

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido())))
                    .andExpect(status().isCreated());

            verify(checkoutService, times(1)).criarPedido(any(PedidoCreatRequest.class));
        }

        @Test
        @DisplayName("deve aceitar pedido com múltiplos itens")
        void deveAceitarPedidoComMultiplosItens() throws Exception {
            PedidoCreatRequest requestMultiplos = new PedidoCreatRequest(
                    null, null, "cliente@email.com", null,
                    List.of(itemRequest(1L, 1), itemRequest(2L, 3), itemRequest(3L, 2))
            );
            when(checkoutService.criarPedido(any())).thenReturn(responsePadrao());

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestMultiplos)))
                    .andExpect(status().isCreated());
        }
    }

    // ─── POST /checkout — validação do Bean Validation ───────────────────────

    @Nested
    @DisplayName("POST /checkout — validação (@Valid)")
    class Validacao {

        @Test
        @DisplayName("deve retornar 400 quando emailCliente está ausente")
        void deveRetornar400SemEmail() throws Exception {
            PedidoCreatRequest semEmail = new PedidoCreatRequest(
                    null, null, null, null,
                    List.of(itemRequest(1L, 1))
            );

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(semEmail)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(checkoutService);
        }

        @Test
        @DisplayName("deve retornar 400 quando emailCliente é inválido")
        void deveRetornar400EmailInvalido() throws Exception {
            PedidoCreatRequest emailInvalido = new PedidoCreatRequest(
                    null, null, "nao-e-um-email", null,
                    List.of(itemRequest(1L, 1))
            );

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(emailInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(checkoutService);
        }

        @Test
        @DisplayName("deve retornar 400 quando a lista de itens está vazia")
        void deveRetornar400SemItens() throws Exception {
            PedidoCreatRequest semItens = new PedidoCreatRequest(
                    null, null, "cliente@email.com", null,
                    List.of()
            );

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(semItens)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(checkoutService);
        }

        @Test
        @DisplayName("deve retornar 400 quando a lista de itens é nula")
        void deveRetornar400ItensNulo() throws Exception {
            PedidoCreatRequest itensNulo = new PedidoCreatRequest(
                    null, null, "cliente@email.com", null,
                    null
            );

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(itensNulo)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(checkoutService);
        }

        @Test
        @DisplayName("deve retornar 400 quando produtoId do item é nulo")
        void deveRetornar400ProdutoIdNulo() throws Exception {
            PedidoCreatRequest requestInvalido = new PedidoCreatRequest(
                    null, null, "cliente@email.com", null,
                    List.of(new ItemPedidoRequest(null, 1))
            );

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(checkoutService);
        }

        @Test
        @DisplayName("deve retornar 400 quando quantidade do item é nula")
        void deveRetornar400QuantidadeNula() throws Exception {
            PedidoCreatRequest requestInvalido = new PedidoCreatRequest(
                    null, null, "cliente@email.com", null,
                    List.of(new ItemPedidoRequest(1L, null))
            );

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(checkoutService);
        }

        @Test
        @DisplayName("deve retornar 400 quando quantidade do item é zero")
        void deveRetornar400QuantidadeZero() throws Exception {
            PedidoCreatRequest requestInvalido = new PedidoCreatRequest(
                    null, null, "cliente@email.com", null,
                    List.of(new ItemPedidoRequest(1L, 0))
            );

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(checkoutService);
        }

        @Test
        @DisplayName("deve rejeitar requisição sem Content-Type application/json")
        void deveRejeitarSemContentType() throws Exception {
            mockMvc.perform(post(URL)
                            .content(objectMapper.writeValueAsString(requestValido())))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        assertThat(status).isIn(415, 500);
                    });
 
            verifyNoInteractions(checkoutService);
        }
    }

    // ─── POST /checkout — erros do service ──────────────────────────────────

    @Nested
    @DisplayName("POST /checkout — erros propagados pelo service")
    class ErrosDoService {

        @Test
        @DisplayName("deve propagar exceção lançada pelo CheckoutService")
        void devePropagarExcecaoDoService() throws Exception {
            when(checkoutService.criarPedido(any()))
                    .thenThrow(new RuntimeException("Erro interno"));

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido())))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("não deve chamar o service quando a validação falha")
        void naoDeveInvocarServiceComRequestInvalido() throws Exception {
            PedidoCreatRequest invalido = new PedidoCreatRequest(
                    null, null, "", null,
                    List.of(itemRequest(1L, 1))
            );

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(checkoutService);
        }
    }
}