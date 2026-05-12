package com.bndesigner.controller.categoria;

import com.bndesigner.dto.request.categoria.CategoriaCreateRequest;
import com.bndesigner.dto.request.categoria.CategoriaUpdateRequest;
import com.bndesigner.dto.response.categoria.CategoriaResponse;
import com.bndesigner.exceptions.ResourceNotFoundException;
import com.bndesigner.service.categoria.CategoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaService categoriaService;

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private CategoriaResponse categoriaResponseFixture() {
        return new CategoriaResponse(1L, "Eletrônicos", "Produtos eletrônicos em geral");
    }

    private CategoriaCreateRequest createRequestFixture() {
        return new CategoriaCreateRequest("Eletrônicos", "Produtos eletrônicos em geral");
    }

    private CategoriaUpdateRequest updateRequestFixture() {
        return new CategoriaUpdateRequest("Eletrônicos Atualizado", "Descrição atualizada");
    }

    // ─── POST /categorias ─────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/categorias → 201 Created com body correto")
    void criar_deveRetornar201ComCategoriaCriada() throws Exception {
        when(categoriaService.criar(any(CategoriaCreateRequest.class)))
                .thenReturn(categoriaResponseFixture());

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestFixture())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCategoria").value(1L))
                .andExpect(jsonPath("$.nome").value("Eletrônicos"))
                .andExpect(jsonPath("$.descricao").value("Produtos eletrônicos em geral"));

        verify(categoriaService).criar(any(CategoriaCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/categorias com nome em branco → 400 Bad Request")
    void criar_comNomeEmBranco_deveRetornar400() throws Exception {
        CategoriaCreateRequest requestInvalido = new CategoriaCreateRequest("", "Descrição válida");

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoriaService);
    }

    @Test
    @DisplayName("POST /api/categorias com nome acima de 45 caracteres → 400 Bad Request")
    void criar_comNomeAcimaDolimite_deveRetornar400() throws Exception {
        CategoriaCreateRequest requestInvalido = new CategoriaCreateRequest(
                "A".repeat(46), "Descrição válida"
        );

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoriaService);
    }

    @Test
    @DisplayName("POST /api/categorias com descricao acima de 100 caracteres → 400 Bad Request")
    void criar_comDescricaoAcimaDolimite_deveRetornar400() throws Exception {
        CategoriaCreateRequest requestInvalido = new CategoriaCreateRequest(
                "Nome válido", "D".repeat(101)
        );

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoriaService);
    }

    @Test
    @DisplayName("POST /api/categorias com descricao nula → 201 Created (descricao é opcional)")
    void criar_comDescricaoNula_deveRetornar201() throws Exception {
        CategoriaCreateRequest requestSemDescricao = new CategoriaCreateRequest("Eletrônicos", null);

        when(categoriaService.criar(any(CategoriaCreateRequest.class)))
                .thenReturn(new CategoriaResponse(1L, "Eletrônicos", null));

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestSemDescricao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").doesNotExist());

        verify(categoriaService).criar(any(CategoriaCreateRequest.class));
    }

    // ─── GET /categorias/{id} ─────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/categorias/{id} → 200 OK com categoria encontrada")
    void buscarPorId_deveRetornar200ComCategoria() throws Exception {
        when(categoriaService.buscarPorId(1L)).thenReturn(categoriaResponseFixture());

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCategoria").value(1L))
                .andExpect(jsonPath("$.nome").value("Eletrônicos"))
                .andExpect(jsonPath("$.descricao").value("Produtos eletrônicos em geral"));

        verify(categoriaService).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/categorias/{id} quando não encontrado → 404 Not Found")
    void buscarPorId_quandoNaoEncontrado_deveRetornar404() throws Exception {
        when(categoriaService.buscarPorId(99L))
                .thenThrow(buscarEntidadePorId(99L));

        mockMvc.perform(get("/api/categorias/99"))
                .andExpect(status().isNotFound());
    }

    // ─── GET /categorias ──────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/categorias → 200 OK com página de categorias")
    void listar_deveRetornar200ComPaginaDeCategorias() throws Exception {
        Page<CategoriaResponse> page = new PageImpl<>(
                List.of(categoriaResponseFixture()),
                PageRequest.of(0, 10),
                1
        );

        when(categoriaService.listar(any())).thenReturn(page);

        mockMvc.perform(get("/api/categorias")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].idCategoria").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(categoriaService).listar(any());
    }

    @Test
    @DisplayName("GET /api/categorias → 200 OK com página vazia")
    void listar_semCategorias_deveRetornarPaginaVazia() throws Exception {
        when(categoriaService.listar(any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    // ─── PUT /categorias/{id} ─────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/categorias/{id} → 200 OK com categoria atualizada")
    void atualizar_deveRetornar200ComCategoriaAtualizada() throws Exception {
        CategoriaResponse responseAtualizado = new CategoriaResponse(1L, "Eletrônicos Atualizado", "Descrição atualizada");

        when(categoriaService.atualizar(eq(1L), any(CategoriaUpdateRequest.class)))
                .thenReturn(responseAtualizado);

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestFixture())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCategoria").value(1L))
                .andExpect(jsonPath("$.nome").value("Eletrônicos Atualizado"))
                .andExpect(jsonPath("$.descricao").value("Descrição atualizada"));

        verify(categoriaService).atualizar(eq(1L), any(CategoriaUpdateRequest.class));
    }

    @Test
    @DisplayName("PUT /api/categorias/{id} com nome em branco → 400 Bad Request")
    void atualizar_comNomeEmBranco_deveRetornar400() throws Exception {
        CategoriaUpdateRequest requestInvalido = new CategoriaUpdateRequest("", "Descrição válida");

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoriaService);
    }

    @Test
    @DisplayName("PUT /api/categorias/{id} com nome acima de 45 caracteres → 400 Bad Request")
    void atualizar_comNomeAcimaDolimite_deveRetornar400() throws Exception {
        CategoriaUpdateRequest requestInvalido = new CategoriaUpdateRequest(
                "A".repeat(46), "Descrição válida"
        );

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoriaService);
    }

    // ─── DELETE /categorias/{id} ──────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/categorias/{id} → 204 No Content")
    void remover_deveRetornar204() throws Exception {
        doNothing().when(categoriaService).deletar(1L);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isNoContent());

        verify(categoriaService).deletar(1L);
    }

    @Test
    @DisplayName("DELETE /api/categorias/{id} quando não encontrado → 404 Not Found")
    void remover_quandoNaoEncontrado_deveRetornar404() throws Exception {
        doThrow(buscarEntidadePorId(99L))
                .when(categoriaService).deletar(99L);

        mockMvc.perform(delete("/api/categorias/99"))
                .andExpect(status().isNotFound());

        verify(categoriaService).deletar(99L);
    }
    
    private ResourceNotFoundException buscarEntidadePorId(Long id) {
		return new ResourceNotFoundException("Arquivo", id);
	}
}