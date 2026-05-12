package com.bndesigner.controller.arquivo;

import com.bndesigner.dto.request.arquivo.ArquivoCreateRequest;
import com.bndesigner.dto.request.arquivo.ArquivoUpdateRequest;
import com.bndesigner.dto.response.arquivo.ArquivoResponse;
import com.bndesigner.exceptions.BusinessException;
import com.bndesigner.exceptions.ResourceNotFoundException;
import com.bndesigner.service.arquivo.ArquivoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArquivoController.class)
@DisplayName("ArquivoController")
class ArquivoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArquivoService arquivoService;

    // -------------------------------------------------------------------------
    // Fixtures
    // -------------------------------------------------------------------------

    private ArquivoCreateRequest buildCreateRequest() {
        return new ArquivoCreateRequest(
                "/uploads/documentos/arquivo.pdf",
                "hash-abc123"
        );
    }

    private ArquivoCreateRequest buildCreateRequestSemHash() {
        return new ArquivoCreateRequest(
                "/uploads/documentos/arquivo.pdf",
                null
        );
    }

    private ArquivoUpdateRequest buildUpdateRequest() {
        return new ArquivoUpdateRequest(
                "Documento Atualizado",
                "Descrição do arquivo atualizado",
                true
        );
    }

    private ArquivoResponse buildArquivoResponse(Long id) {
        return new ArquivoResponse(
                id,
                "/uploads/documentos/arquivo.pdf",
                "hash-abc123",
                LocalDateTime.of(2025, 1, 15, 10, 30),
                true
        );
    }

    // =========================================================================
    // POST /api/arquivos
    // =========================================================================

    @Nested
    @DisplayName("POST /api/arquivos")
    class Criar {

        @Test
        @DisplayName("deve retornar 201 e o ArquivoResponse ao criar com sucesso")
        void deveCriarArquivoComSucesso() throws Exception {

            ArquivoCreateRequest request = buildCreateRequest();
            ArquivoResponse response = buildArquivoResponse(1L);

            when(arquivoService.criar(any(ArquivoCreateRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/arquivos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idArquivo").value(1L))
                    .andExpect(jsonPath("$.caminhoArquivo").value("/uploads/documentos/arquivo.pdf"))
                    .andExpect(jsonPath("$.hashArquivo").value("hash-abc123"))
                    .andExpect(jsonPath("$.ativo").value(true));

            verify(arquivoService, times(1)).criar(any(ArquivoCreateRequest.class));
        }

        @Test
        @DisplayName("deve retornar 201 quando hashArquivo for nulo (campo opcional)")
        void deveCriarArquivoSemHash() throws Exception {

            ArquivoCreateRequest request = buildCreateRequestSemHash();
            ArquivoResponse response = new ArquivoResponse(
                    2L, "/uploads/documentos/arquivo.pdf", null,
                    LocalDateTime.of(2025, 1, 15, 10, 30), true);

            when(arquivoService.criar(any(ArquivoCreateRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/arquivos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.hashArquivo").doesNotExist());
        }

        @Test
        @DisplayName("deve retornar 409 quando hash já estiver cadastrado")
        void deveRetornar409QuandoHashDuplicado() throws Exception {

            when(arquivoService.criar(any(ArquivoCreateRequest.class)))
                    .thenThrow(new BusinessException(
                            HttpStatus.CONFLICT,
                            "Hash já cadastrado!",
                            "Já existe um arquivo com esse hash"));

            mockMvc.perform(post("/api/arquivos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(buildCreateRequest())))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("deve retornar 400 quando caminhoArquivo estiver em branco")
        void deveRetornar400QuandoCaminhoEmBranco() throws Exception {

            ArquivoCreateRequest requestInvalido = new ArquivoCreateRequest("", "hash-abc123");

            mockMvc.perform(post("/api/arquivos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(arquivoService);
        }

        @Test
        @DisplayName("deve retornar 400 quando caminhoArquivo ultrapassar 255 caracteres")
        void deveRetornar400QuandoCaminhoMuitoLongo() throws Exception {

            String caminhoLongo = "/uploads/".concat("a".repeat(250));
            ArquivoCreateRequest requestInvalido = new ArquivoCreateRequest(caminhoLongo, null);

            mockMvc.perform(post("/api/arquivos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(arquivoService);
        }

        @Test
        @DisplayName("deve retornar 400 quando hashArquivo ultrapassar 100 caracteres")
        void deveRetornar400QuandoHashMuitoLongo() throws Exception {

            String hashLongo = "h".repeat(101);
            ArquivoCreateRequest requestInvalido =
                    new ArquivoCreateRequest("/uploads/arquivo.pdf", hashLongo);

            mockMvc.perform(post("/api/arquivos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(arquivoService);
        }
    }

    // =========================================================================
    // GET /api/arquivos/{id}
    // =========================================================================

    @Nested
    @DisplayName("GET /api/arquivos/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar 200 e o ArquivoResponse quando encontrado")
        void deveRetornarArquivoQuandoEncontrado() throws Exception {

            when(arquivoService.buscarPorId(1L)).thenReturn(buildArquivoResponse(1L));

            mockMvc.perform(get("/api/arquivos/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idArquivo").value(1L))
                    .andExpect(jsonPath("$.caminhoArquivo").value("/uploads/documentos/arquivo.pdf"))
                    .andExpect(jsonPath("$.hashArquivo").value("hash-abc123"))
                    .andExpect(jsonPath("$.ativo").value(true));

            verify(arquivoService, times(1)).buscarPorId(1L);
        }

        @Test
        @DisplayName("deve retornar 404 quando arquivo não encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {

            when(arquivoService.buscarPorId(99L))
                    .thenThrow(buscarEntidadePorId(99L));

            mockMvc.perform(get("/api/arquivos/99"))
                    .andExpect(status().isNotFound());

            verify(arquivoService, times(1)).buscarPorId(99L);
        }
    }

    // =========================================================================
    // GET /api/arquivos
    // =========================================================================

    @Nested
    @DisplayName("GET /api/arquivos")
    class ListarAtivos {

        @Test
        @DisplayName("deve retornar 200 e página com arquivos ativos")
        void deveRetornarPaginaDeArquivosAtivos() throws Exception {

            List<ArquivoResponse> content = List.of(
                    buildArquivoResponse(1L),
                    buildArquivoResponse(2L)
            );
            PageImpl<ArquivoResponse> page =
                    new PageImpl<>(content, PageRequest.of(0, 10), 2);

            when(arquivoService.listarAtivos(any())).thenReturn(page);

            mockMvc.perform(get("/api/arquivos")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.content[0].idArquivo").value(1L))
                    .andExpect(jsonPath("$.content[1].idArquivo").value(2L));

            verify(arquivoService, times(1)).listarAtivos(any());
        }

        @Test
        @DisplayName("deve retornar 200 com página vazia quando não houver arquivos ativos")
        void deveRetornarPaginaVaziaQuandoSemAtivos() throws Exception {

            PageImpl<ArquivoResponse> emptyPage =
                    new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

            when(arquivoService.listarAtivos(any())).thenReturn(emptyPage);

            mockMvc.perform(get("/api/arquivos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(0))
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }

    // =========================================================================
    // PUT /api/arquivos/{id}
    // =========================================================================

    @Nested
    @DisplayName("PUT /api/arquivos/{id}")
    class Atualizar {

        @Test
        @DisplayName("deve retornar 200 e o ArquivoResponse atualizado")
        void deveAtualizarArquivoComSucesso() throws Exception {

            ArquivoUpdateRequest request = buildUpdateRequest();
            ArquivoResponse response = buildArquivoResponse(1L);

            when(arquivoService.atualizar(eq(1L), any(ArquivoUpdateRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(put("/api/arquivos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idArquivo").value(1L))
                    .andExpect(jsonPath("$.caminhoArquivo").value("/uploads/documentos/arquivo.pdf"));

            verify(arquivoService, times(1)).atualizar(eq(1L), any(ArquivoUpdateRequest.class));
        }

        @Test
        @DisplayName("deve retornar 400 quando nome estiver em branco")
        void deveRetornar400QuandoNomeEmBranco() throws Exception {

            ArquivoUpdateRequest requestInvalido =
                    new ArquivoUpdateRequest("", "Descrição válida", true);

            mockMvc.perform(put("/api/arquivos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(arquivoService);
        }

        @Test
        @DisplayName("deve retornar 400 quando nome ultrapassar 45 caracteres")
        void deveRetornar400QuandoNomeMuitoLongo() throws Exception {

            ArquivoUpdateRequest requestInvalido =
                    new ArquivoUpdateRequest("n".repeat(46), "Descrição válida", true);

            mockMvc.perform(put("/api/arquivos/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(arquivoService);
        }

        @Test
        @DisplayName("deve retornar 404 ao atualizar arquivo inexistente")
        void deveRetornar404AoAtualizarInexistente() throws Exception {

            when(arquivoService.atualizar(eq(99L), any(ArquivoUpdateRequest.class)))
                    .thenThrow(buscarEntidadePorId(99L));

            mockMvc.perform(put("/api/arquivos/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(buildUpdateRequest())))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /api/arquivos/{id}/desativar")
    class Desativar {

        @Test
        @DisplayName("deve retornar 204 ao desativar arquivo com sucesso")
        void deveDesativarArquivoComSucesso() throws Exception {

            doNothing().when(arquivoService).desativar(1L);

            mockMvc.perform(patch("/api/arquivos/1/desativar"))
                    .andExpect(status().isNoContent());

            verify(arquivoService, times(1)).desativar(1L);
        }

        @Test
        @DisplayName("deve retornar 404 ao desativar arquivo inexistente")
        void deveRetornar404AoDesativarInexistente() throws Exception {

            doThrow(buscarEntidadePorId(99L))
                    .when(arquivoService).desativar(99L);

            mockMvc.perform(patch("/api/arquivos/99/desativar"))
                    .andExpect(status().isNotFound());

            verify(arquivoService, times(1)).desativar(99L);
        }
    }
    
    private ResourceNotFoundException buscarEntidadePorId(Long id) {
		return new ResourceNotFoundException("Arquivo", id);
	}
    
}