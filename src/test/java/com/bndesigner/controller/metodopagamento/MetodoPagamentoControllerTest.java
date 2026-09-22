package com.bndesigner.controller.metodopagamento;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bndesigner.dto.request.metodopagamento.MetodoPagamentoCreateRequest;
import com.bndesigner.dto.request.metodopagamento.MetodoPagamentoUpdateRequest;
import com.bndesigner.dto.response.metodopagamento.MetodoPagamentoResponse;
import com.bndesigner.service.metodopagamento.impl.MetodoPagamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MetodoPagamentoController.class)
@DisplayName("MetodoPagamentoController")
class MetodoPagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MetodoPagamentoService metodoPagamentoService;

    private final MetodoPagamentoResponse response =
            new MetodoPagamentoResponse(
                    1L,
                    "PIX",
                    "Pagamento via Pix",
                    true);

    @Nested
    @DisplayName("POST /api/metodos-pagamento")
    class Criar {

        @Test
        @DisplayName("deve criar método de pagamento e retornar 201")
        void deveCriarMetodoPagamento() throws Exception {

            MetodoPagamentoCreateRequest request =
                    new MetodoPagamentoCreateRequest(
                            "PIX",
                            "Pagamento via Pix",
                            true);

            when(metodoPagamentoService.criar(any()))
                    .thenReturn(response);

            mockMvc.perform(
                    post("/api/metodos-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                            {
                                "idMetodoPagamento": 1,
                                "nomeMetodoPagamento": "PIX",
                                "descricaoMetodoPagamento": "Pagamento via Pix",
                                "ativo": true
                            }
                            """));

            verify(metodoPagamentoService).criar(any());
        }

        @Test
        @DisplayName("deve retornar 400 quando nome não informado")
        void deveRetornar400QuandoNomeNaoInformado() throws Exception {

            MetodoPagamentoCreateRequest request =
                    new MetodoPagamentoCreateRequest(
                            "",
                            "Pagamento via Pix",
                            true);

            mockMvc.perform(
                    post("/api/metodos-pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/metodos-pagamento/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("deve buscar método e retornar 200")
        void deveBuscarMetodo() throws Exception {

            when(metodoPagamentoService.buscarPorId(1L))
                    .thenReturn(response);

            mockMvc.perform(
                    get("/api/metodos-pagamento/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                            {
                                "idMetodoPagamento": 1,
                                "nomeMetodoPagamento": "PIX",
                                "descricaoMetodoPagamento": "Pagamento via Pix",
                                "ativo": true
                            }
                            """));

            verify(metodoPagamentoService).buscarPorId(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/metodos-pagamento")
    class Listar {

        @Test
        @DisplayName("deve listar métodos e retornar 200")
        void deveListarMetodos() throws Exception {

            MetodoPagamentoResponse boleto =
                    new MetodoPagamentoResponse(
                            2L,
                            "Boleto",
                            "Pagamento via boleto",
                            false);

            when(metodoPagamentoService.listar())
                    .thenReturn(List.of(response, boleto));

            mockMvc.perform(
                    get("/api/metodos-pagamento"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                            [
                                {
                                    "idMetodoPagamento": 1,
                                    "nomeMetodoPagamento": "PIX",
                                    "descricaoMetodoPagamento": "Pagamento via Pix",
                                    "ativo": true
                                },
                                {
                                    "idMetodoPagamento": 2,
                                    "nomeMetodoPagamento": "Boleto",
                                    "descricaoMetodoPagamento": "Pagamento via boleto",
                                    "ativo": false
                                }
                            ]
                            """));

            verify(metodoPagamentoService).listar();
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não existem métodos")
        void deveRetornarListaVazia() throws Exception {

            when(metodoPagamentoService.listar())
                    .thenReturn(List.of());

            mockMvc.perform(
                    get("/api/metodos-pagamento"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[]"));

            verify(metodoPagamentoService).listar();
        }
    }

    @Nested
    @DisplayName("PUT /api/metodos-pagamento/{id}")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar método e retornar 200")
        void deveAtualizarMetodo() throws Exception {

            MetodoPagamentoUpdateRequest request =
                    new MetodoPagamentoUpdateRequest(
                            "PIX",
                            "Pagamento via Pix atualizado",
                            true);

            MetodoPagamentoResponse atualizado =
                    new MetodoPagamentoResponse(
                            1L,
                            "PIX",
                            "Pagamento via Pix atualizado",
                            true);

            when(metodoPagamentoService.atualizar(
                    eq(1L), any()))
                    .thenReturn(atualizado);

            mockMvc.perform(
                    put("/api/metodos-pagamento/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json("""
                            {
                                "idMetodoPagamento": 1,
                                "nomeMetodoPagamento": "PIX",
                                "descricaoMetodoPagamento": "Pagamento via Pix atualizado",
                                "ativo": true
                            }
                            """));

            verify(metodoPagamentoService)
                    .atualizar(eq(1L), any());
        }

        @Test
        @DisplayName("deve retornar 400 quando nome não informado")
        void deveRetornar400QuandoNomeNaoInformado() throws Exception {

            MetodoPagamentoUpdateRequest request =
                    new MetodoPagamentoUpdateRequest(
                            "",
                            "Descrição",
                            true);

            mockMvc.perform(
                    put("/api/metodos-pagamento/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/metodos-pagamento/{id}/desativar")
    class Desativar {

        @Test
        @DisplayName("deve desativar método e retornar 204")
        void deveDesativarMetodo() throws Exception {

            doNothing()
                    .when(metodoPagamentoService)
                    .desativar(1L);

            mockMvc.perform(
                    patch("/api/metodos-pagamento/1/desativar"))
                    .andExpect(status().isNoContent());

            verify(metodoPagamentoService)
                    .desativar(1L);
        }
    }
}