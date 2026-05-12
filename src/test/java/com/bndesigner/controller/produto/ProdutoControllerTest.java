package com.bndesigner.controller.produto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import com.bndesigner.dto.request.produto.ProdutoCreateRequest;
import com.bndesigner.dto.request.produto.ProdutoUpdateRequest;
import com.bndesigner.dto.response.produto.ProdutoResponse;
import com.bndesigner.service.produto.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProdutoService produtoService;

    private ProdutoResponse criarResponse() {
        return new ProdutoResponse(
                1L,
                "Notebook Gamer",
                "RTX 4060",
                new BigDecimal("7500.00"),
                LocalDateTime.now(),
                1L,
                "Informática",
                1L
        );
    }

    @Nested
    class Criar {

        @Test
        @DisplayName("Deve criar produto com sucesso")
        void deveCriarProdutoComSucesso() throws Exception {

            ProdutoCreateRequest request =
                    new ProdutoCreateRequest(
                            "Notebook Gamer",
                            "RTX 4060",
                            new BigDecimal("7500.00"),
                            1L,
                            1L
                    );

            when(produtoService.criar(any()))
                    .thenReturn(criarResponse());

            mockMvc.perform(post("/api/produtos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.titulo").value("Notebook Gamer"))
                    .andExpect(jsonPath("$.categoriaId").value(1))
                    .andExpect(jsonPath("$.categoriaNome").value("Informática"))
                    .andExpect(jsonPath("$.arquivoId").value(1));
        }

        @Test
        @DisplayName("Deve retornar 400 ao criar produto inválido")
        void deveRetornar400AoCriarProdutoInvalido() throws Exception {

            ProdutoCreateRequest request =
                    new ProdutoCreateRequest(
                            "",
                            "Descricao",
                            new BigDecimal("0"),
                            null,
                            null
                    );

            mockMvc.perform(post("/api/produtos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class BuscarPorId {

        @Test
        @DisplayName("Deve buscar produto por id")
        void deveBuscarProdutoPorId() throws Exception {

            when(produtoService.buscarPorId(1L))
                    .thenReturn(criarResponse());

            mockMvc.perform(get("/api/produtos/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.titulo").value("Notebook Gamer"))
                    .andExpect(jsonPath("$.categoriaNome").value("Informática"));
        }
    }

    @Nested
    class Listar {

        @Test
        @DisplayName("Deve listar produtos paginados")
        void deveListarProdutos() throws Exception {

            var page =
                    new PageImpl<>(
                            List.of(criarResponse()),
                            PageRequest.of(0, 10),
                            1
                    );

            when(produtoService.listar(any()))
                    .thenReturn(page);

            mockMvc.perform(get("/api/produtos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(1))
                    .andExpect(jsonPath("$.content[0].titulo")
                            .value("Notebook Gamer"))
                    .andExpect(jsonPath("$.content[0].categoriaNome")
                            .value("Informática"));
        }
    }

    @Nested
    class ListarPorCategoria {

        @Test
        @DisplayName("Deve listar produtos por categoria")
        void deveListarProdutosPorCategoria() throws Exception {

            var page =
                    new PageImpl<>(
                            List.of(criarResponse()),
                            PageRequest.of(0, 10),
                            1
                    );

            when(produtoService.listarPorCategoria(eq(1L), any()))
                    .thenReturn(page);

            mockMvc.perform(get("/api/produtos/categoria/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].categoriaId")
                            .value(1))
                    .andExpect(jsonPath("$.content[0].categoriaNome")
                            .value("Informática"));
        }
    }

    @Nested
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar produto")
        void deveAtualizarProduto() throws Exception {

            ProdutoUpdateRequest request =
                    new ProdutoUpdateRequest(
                            "Notebook Atualizado",
                            "Nova descricao",
                            new BigDecimal("8000.00"),
                            1L,
                            1L
                    );

            ProdutoResponse response =
                    new ProdutoResponse(
                            1L,
                            "Notebook Atualizado",
                            "Nova descricao",
                            new BigDecimal("8000.00"),
                            LocalDateTime.now(),
                            1L,
                            "Informática",
                            1L
                    );

            when(produtoService.atualizar(eq(1L), any()))
                    .thenReturn(response);

            mockMvc.perform(put("/api/produtos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.titulo")
                            .value("Notebook Atualizado"))
                    .andExpect(jsonPath("$.categoriaNome")
                            .value("Informática"));
        }
    }

    @Nested
    class Deletar {

        @Test
        @DisplayName("Deve deletar produto")
        void deveDeletarProduto() throws Exception {

            doNothing()
                    .when(produtoService)
                    .deletar(1L);

            mockMvc.perform(delete("/api/produtos/1"))
                    .andExpect(status().isNoContent());
        }
    }
}