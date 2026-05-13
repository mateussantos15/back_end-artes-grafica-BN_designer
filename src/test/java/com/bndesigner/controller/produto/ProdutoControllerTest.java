package com.bndesigner.controller.produto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bndesigner.exceptions.ResourceNotFoundException;
import com.bndesigner.dto.response.produto.ProdutoResponse;
import com.bndesigner.service.produto.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProdutoController.class)
@DisplayName("ProdutoController")
class ProdutoControllerTest {

    // ─────────────────────────────────────────────────────────────────────────
    // Infraestrutura do slice
    // ─────────────────────────────────────────────────────────────────────────

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProdutoService produtoService;

    // ─────────────────────────────────────────────────────────────────────────
    // Fixtures
    // ─────────────────────────────────────────────────────────────────────────

    private static final String BASE_URL = "/api/produtos";

    private ProdutoResponse responseFake() {
        return new ProdutoResponse(
                1L,
                "Logo Premium",
                "Logotipo profissional",
                new BigDecimal("49.90"),
                LocalDateTime.of(2025, 1, 10, 12, 0),
                LocalDateTime.of(2025, 10, 3, 10, 0),
                2L,
                "Design",
                10L
        );
    }

    /** Body JSON válido para criação */
    private String createRequestJson() {
        return """
                {
                    "titulo": "Logo Premium",
                    "descricao": "Logotipo profissional",
                    "preco": 49.90,
                    "categoriaId": 2,
                    "arquivoId": 10
                }
                """;
    }

    /** Body JSON válido para atualização */
    private String updateRequestJson() {
        return """
                {
                    "titulo": "Logo Atualizado",
                    "descricao": "Nova descrição",
                    "preco": 59.90,
                    "categoriaId": 2
                }
                """;
    }

    // =========================================================================
    // POST /api/produtos
    // =========================================================================

    @Nested
    @DisplayName("POST /api/produtos")
    class Criar {

        @Test
        @DisplayName("deve retornar 201 e o ProdutoResponse quando request é válido")
        void deveRetornar201ComResponseValido() throws Exception {
            // Arrange
            when(produtoService.criar(any())).thenReturn(responseFake());

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createRequestJson()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.titulo").value("Logo Premium"))
                    .andExpect(jsonPath("$.preco").value(49.90))
                    .andExpect(jsonPath("$.categoriaId").value(2))
                    .andExpect(jsonPath("$.arquivoId").value(10));
        }

        @Test
        @DisplayName("deve retornar 400 quando titulo está em branco")
        void deveRetornar400QuandoTituloEmBranco() throws Exception {
            // Arrange
            String body = """
                    {
                        "titulo": "",
                        "preco": 49.90,
                        "categoriaId": 2
                    }
                    """;

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 400 quando titulo está ausente")
        void deveRetornar400QuandoTituloAusente() throws Exception {
            // Arrange
            String body = """
                    {
                        "preco": 49.90,
                        "categoriaId": 2
                    }
                    """;

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 400 quando preco é nulo")
        void deveRetornar400QuandoPrecoNulo() throws Exception {
            // Arrange
            String body = """
                    {
                        "titulo": "Logo Premium",
                        "categoriaId": 2
                    }
                    """;

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 400 quando preco é zero")
        void deveRetornar400QuandoPrecoZero() throws Exception {
            // Arrange
            String body = """
                    {
                        "titulo": "Logo Premium",
                        "preco": 0.00,
                        "categoriaId": 2
                    }
                    """;

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 400 quando categoriaId é nulo")
        void deveRetornar400QuandoCategoriaIdNulo() throws Exception {
            // Arrange
            String body = """
                    {
                        "titulo": "Logo Premium",
                        "preco": 49.90
                    }
                    """;

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 400 quando body está ausente")
        void deveRetornar400QuandoBodyAusente() throws Exception {
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve aceitar arquivoId nulo (campo opcional)")
        void deveAceitarArquivoIdNulo() throws Exception {
            // Arrange
            String body = """
                    {
                        "titulo": "Logo Premium",
                        "preco": 49.90,
                        "categoriaId": 2
                    }
                    """;
            when(produtoService.criar(any())).thenReturn(responseFake());

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());
        }
    }

    // =========================================================================
    // GET /api/produtos/{id}
    // =========================================================================

    @Nested
    @DisplayName("GET /api/produtos/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar 200 e o produto quando id existe")
        void deveRetornar200QuandoIdExiste() throws Exception {
            // Arrange
            when(produtoService.buscarPorId(1L)).thenReturn(responseFake());

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.titulo").value("Logo Premium"));
        }

        @Test
        @DisplayName("deve retornar 404 quando produto não existe")
        void deveRetornar404QuandoProdutoNaoExiste() throws Exception {
            // Arrange
            when(produtoService.buscarPorId(999L))
                    .thenThrow(new ResourceNotFoundException("Produto", 999L));

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/999"))
                    .andExpect(status().isNotFound());
        }
    }

    // =========================================================================
    // GET /api/produtos
    // =========================================================================

    @Nested
    @DisplayName("GET /api/produtos")
    class Listar {

        @Test
        @DisplayName("deve retornar 200 com página de produtos")
        void deveRetornar200ComPagina() throws Exception {
            // Arrange
            var pageable = PageRequest.of(0, 10);
            var page = new PageImpl<>(List.of(responseFake()), pageable, 1);
            when(produtoService.listar(any())).thenReturn(page);

            // Act & Assert
            mockMvc.perform(get(BASE_URL)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("deve retornar 200 com página vazia quando não há produtos")
        void deveRetornar200ComPaginaVazia() throws Exception {
            // Arrange
            var page = new PageImpl<ProdutoResponse>(List.of());
            when(produtoService.listar(any())).thenReturn(page);

            // Act & Assert
            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }

    // =========================================================================
    // GET /api/produtos/categoria/{idCategoria}
    // =========================================================================

    @Nested
    @DisplayName("GET /api/produtos/categoria/{idCategoria}")
    class ListarPorCategoria {

        @Test
        @DisplayName("deve retornar 200 com produtos filtrados pela categoria")
        void deveRetornar200ComProdutosDaCategoria() throws Exception {
            // Arrange
            var pageable = PageRequest.of(0, 10);
            var page = new PageImpl<>(List.of(responseFake()), pageable, 1);
            when(produtoService.listarPorCategoria(eq(2L), any())).thenReturn(page);

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/categoria/2")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].categoriaId").value(2L))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("deve retornar 404 quando categoria não existe")
        void deveRetornar404QuandoCategoriaNaoExiste() throws Exception {
            // Arrange
            when(produtoService.listarPorCategoria(eq(999L), any()))
                    .thenThrow(new ResourceNotFoundException("Categoria", 999L));

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/categoria/999"))
                    .andExpect(status().isNotFound());
        }
    }

    // =========================================================================
    // PUT /api/produtos/{id}
    // =========================================================================

    @Nested
    @DisplayName("PUT /api/produtos/{id}")
    class Atualizar {

        @Test
        @DisplayName("deve retornar 200 e o produto atualizado quando request é válido")
        void deveRetornar200ComProdutoAtualizado() throws Exception {
            // Arrange
            ProdutoResponse atualizado = new ProdutoResponse(
                    1L, "Logo Atualizado", "Nova descrição",
                    new BigDecimal("59.90"),
                    LocalDateTime.of(2025, 1, 10, 12, 0),
                    LocalDateTime.of(2025, 10, 3, 10, 0),
                    2L, "Design", null);

            when(produtoService.atualizar(eq(1L), any())).thenReturn(atualizado);

            // Act & Assert
            mockMvc.perform(put(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateRequestJson()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.titulo").value("Logo Atualizado"))
                    .andExpect(jsonPath("$.preco").value(59.90));
        }

        @Test
        @DisplayName("deve retornar 404 quando produto não existe")
        void deveRetornar404QuandoProdutoNaoExiste() throws Exception {
            // Arrange
            when(produtoService.atualizar(eq(999L), any()))
                    .thenThrow(new ResourceNotFoundException("Produto", 999L));

            // Act & Assert
            mockMvc.perform(put(BASE_URL + "/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateRequestJson()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("deve retornar 400 quando titulo está em branco")
        void deveRetornar400QuandoTituloEmBranco() throws Exception {
            // Arrange
            String body = """
                    {
                        "titulo": "",
                        "preco": 59.90,
                        "categoriaId": 2
                    }
                    """;

            // Act & Assert
            mockMvc.perform(put(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 400 quando preco é menor que 0.01")
        void deveRetornar400QuandoPrecoInvalido() throws Exception {
            // Arrange
            String body = """
                    {
                        "titulo": "Logo Atualizado",
                        "preco": -1.00,
                        "categoriaId": 2
                    }
                    """;

            // Act & Assert
            mockMvc.perform(put(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 400 quando categoriaId é nulo")
        void deveRetornar400QuandoCategoriaIdNulo() throws Exception {
            // Arrange
            String body = """
                    {
                        "titulo": "Logo Atualizado",
                        "preco": 59.90
                    }
                    """;

            // Act & Assert
            mockMvc.perform(put(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }
    }

    // =========================================================================
    // DELETE /api/produtos/{id}
    // =========================================================================

    @Nested
    @DisplayName("DELETE /api/produtos/{id}")
    class Deletar {

        @Test
        @DisplayName("deve retornar 204 quando produto existe")
        void deveRetornar204QuandoProdutoExiste() throws Exception {
            // Arrange
            doNothing().when(produtoService).deletar(1L);

            // Act & Assert
            mockMvc.perform(delete(BASE_URL + "/1"))
                    .andExpect(status().isNoContent());

            verify(produtoService).deletar(1L);
        }

        @Test
        @DisplayName("deve retornar 404 quando produto não existe")
        void deveRetornar404QuandoProdutoNaoExiste() throws Exception {
            // Arrange
            doThrow(new ResourceNotFoundException("Produto", 999L))
                    .when(produtoService).deletar(999L);

            // Act & Assert
            mockMvc.perform(delete(BASE_URL + "/999"))
                    .andExpect(status().isNotFound());
        }
    }
}