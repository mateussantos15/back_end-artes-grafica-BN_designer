package com.bndesigner.controller.cupom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.bndesigner.dto.request.cupom.CupomCreateRequest;
import com.bndesigner.dto.request.cupom.CupomUpdateRequest;
import com.bndesigner.dto.response.cupom.CupomResponse;
import com.bndesigner.dto.response.cupom.ValidacaoCupomResponse;
import com.bndesigner.exceptions.BusinessException;
import com.bndesigner.exceptions.ResourceNotFoundException;
import com.bndesigner.service.cupom.CupomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bndesigner.domain.enums.cupom.StatusCupom;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CupomController.class)
@DisplayName("CupomController")
class CupomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CupomService cupomService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ---------- helpers de fixture ----------

    private CupomResponse responseValido() {
        return new CupomResponse(
                1L,
                "PROMO10",
                new BigDecimal("10.00"),
                LocalDate.now().plusDays(10),
                StatusCupom.ATIVO
        );
    }

    // =========================================================
    //  POST /api/cupons
    // =========================================================
    @Nested
    @DisplayName("POST /api/cupons")
    class Criar {

        private CupomCreateRequest requestValido;

        @BeforeEach
        void setUp() {
            requestValido = new CupomCreateRequest(
                    "PROMO10",
                    new BigDecimal("10.00"),
                    LocalDate.now().plusDays(10)
            );
        }

        @Test
        @DisplayName("deve retornar 200 e CupomResponse quando criação é bem-sucedida")
        void deveRetornar200QuandoCriacaoBemSucedida() throws Exception {
            // Arrange
            when(cupomService.criar(any(CupomCreateRequest.class))).thenReturn(responseValido());

            // Act & Assert
            mockMvc.perform(post("/api/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.codigo").value("PROMO10"));

            verify(cupomService).criar(any(CupomCreateRequest.class));
        }

        @Test
        @DisplayName("deve retornar 409 quando código já está cadastrado")
        void deveRetornar409QuandoCodigoDuplicado() throws Exception {
            // Arrange
            when(cupomService.criar(any(CupomCreateRequest.class)))
                    .thenThrow(new BusinessException(HttpStatus.CONFLICT,
                            "Código de cupom já cadastrado",
                            "Já existe um cupom com esse código: 'PROMO10'"));

            // Act & Assert
            mockMvc.perform(post("/api/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("deve retornar 400 quando data de validade já expirou")
        void deveRetornar400QuandoDataExpirada() throws Exception {
            // Arrange
            when(cupomService.criar(any(CupomCreateRequest.class)))
                    .thenThrow(new BusinessException(HttpStatus.BAD_REQUEST,
                            "A validade do código expirou",
                            "A validade do cupom expirou em: '2020-01-01'."));

            // Act & Assert
            mockMvc.perform(post("/api/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 400 quando body é inválido (Bean Validation)")
        void deveRetornar400QuandoBodyInvalido() throws Exception {
            // Arrange — envia JSON vazio, sem os campos obrigatórios
            mockMvc.perform(post("/api/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(cupomService);
        }
    }

    // =========================================================
    //  GET /api/cupons
    // =========================================================
    @Nested
    @DisplayName("GET /api/cupons")
    class Listar {

        @Test
        @DisplayName("deve retornar 200 e página de cupons")
        void deveRetornar200ComPaginaDeCupons() throws Exception {
            // Arrange
            Page<CupomResponse> pagina = new PageImpl<>(
                    List.of(responseValido()),
                    PageRequest.of(0, 10),
                    1
            );
            when(cupomService.listar(any(Pageable.class))).thenReturn(pagina);

            // Act & Assert
            mockMvc.perform(get("/api/cupons")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].codigo").value("PROMO10"))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(cupomService).listar(any(Pageable.class));
        }

        @Test
        @DisplayName("deve retornar 200 com página vazia quando não há cupons")
        void deveRetornar200ComPaginaVazia() throws Exception {
            // Arrange
            when(cupomService.listar(any(Pageable.class))).thenReturn(Page.empty());

            // Act & Assert
            mockMvc.perform(get("/api/cupons"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }

    // =========================================================
    //  GET /api/cupons/{id}
    // =========================================================
    @Nested
    @DisplayName("GET /api/cupons/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar 200 e CupomResponse quando cupom existe")
        void deveRetornar200QuandoCupomExiste() throws Exception {
            // Arrange
            when(cupomService.buscarPorId(1L)).thenReturn(responseValido());

            // Act & Assert
            mockMvc.perform(get("/api/cupons/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.codigo").value("PROMO10"));

            verify(cupomService).buscarPorId(1L);
        }

        @Test
        @DisplayName("deve retornar 404 quando cupom não existe")
        void deveRetornar404QuandoCupomNaoExiste() throws Exception {
            // Arrange
            when(cupomService.buscarPorId(99L))
                    .thenThrow(new ResourceNotFoundException("Cupom", 99L));

            // Act & Assert
            mockMvc.perform(get("/api/cupons/99"))
                    .andExpect(status().isNotFound());

            verify(cupomService).buscarPorId(99L);
        }
    }

    // =========================================================
    //  PUT /api/cupons/{id}
    // =========================================================
    @Nested
    @DisplayName("PUT /api/cupons/{id}")
    class Atualizar {

        private CupomUpdateRequest requestValido;

        @BeforeEach
        void setUp() {
            requestValido = new CupomUpdateRequest(
                    "PROMO10",
                    new BigDecimal("10.00"),
                    LocalDate.now().plusDays(5),
                    StatusCupom.ATIVO
            );
        }

        @Test
        @DisplayName("deve retornar 200 e CupomResponse quando atualização é bem-sucedida")
        void deveRetornar200QuandoAtualizacaoBemSucedida() throws Exception {
            // Arrange
            when(cupomService.atualizar(eq(1L), any(CupomUpdateRequest.class)))
                    .thenReturn(responseValido());

            // Act & Assert
            mockMvc.perform(put("/api/cupons/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.codigo").value("PROMO10"));

            verify(cupomService).atualizar(eq(1L), any(CupomUpdateRequest.class));
        }

        @Test
        @DisplayName("deve retornar 404 quando cupom não existe")
        void deveRetornar404QuandoCupomNaoExiste() throws Exception {
            // Arrange
            when(cupomService.atualizar(eq(99L), any(CupomUpdateRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Cupom", 99L));

            // Act & Assert
            mockMvc.perform(put("/api/cupons/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("deve retornar 409 quando novo código já pertence a outro cupom")
        void deveRetornar409QuandoCodigoDuplicado() throws Exception {
            // Arrange
            when(cupomService.atualizar(eq(1L), any(CupomUpdateRequest.class)))
                    .thenThrow(new BusinessException(HttpStatus.CONFLICT,
                            "Código de cupom já cadastrado",
                            "Já existe um cupom com esse código: 'PROMO10'"));

            // Act & Assert
            mockMvc.perform(put("/api/cupons/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("deve retornar 400 quando body é inválido (Bean Validation)")
        void deveRetornar400QuandoBodyInvalido() throws Exception {
            mockMvc.perform(put("/api/cupons/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(cupomService);
        }
    }

    // =========================================================
    //  DELETE /api/cupons/{id}
    // =========================================================
    @Nested
    @DisplayName("DELETE /api/cupons/{id}")
    class Deletar {

        @Test
        @DisplayName("deve retornar 200 quando cupom é deletado com sucesso")
        void deveRetornar200QuandoDeletadoComSucesso() throws Exception {
            // Arrange
            doNothing().when(cupomService).deletar(1L);

            // Act & Assert
            mockMvc.perform(delete("/api/cupons/1"))
                    .andExpect(status().isOk());

            verify(cupomService).deletar(1L);
        }

        @Test
        @DisplayName("deve retornar 404 quando cupom não existe")
        void deveRetornar404QuandoCupomNaoExiste() throws Exception {
            // Arrange
            doThrow(new ResourceNotFoundException("Cupom", 99L))
                    .when(cupomService).deletar(99L);

            // Act & Assert
            mockMvc.perform(delete("/api/cupons/99"))
                    .andExpect(status().isNotFound());

            verify(cupomService).deletar(99L);
        }
    }

    // =========================================================
    //  GET /api/cupons/validar/{codigo}
    // =========================================================
    @Nested
    @DisplayName("GET /api/cupons/validar/{codigo}")
    class ValidarCupom {

        @Test
        @DisplayName("deve retornar 200 e ValidacaoCupomResponse quando cupom é válido")
        void deveRetornar200QuandoCupomValido() throws Exception {
            // Arrange
            ValidacaoCupomResponse validacaoResponse = new ValidacaoCupomResponse(
                    true,
                    "PROMO10",
                    new BigDecimal("10.00"),
                    "Cupom válido"
            );
            when(cupomService.validarCupom("PROMO10")).thenReturn(validacaoResponse);

            // Act & Assert
            mockMvc.perform(get("/api/cupons/validar/PROMO10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valido").value(true))
                    .andExpect(jsonPath("$.codigo").value("PROMO10"))
                    .andExpect(jsonPath("$.descontoPercentual").value(10.00))
                    .andExpect(jsonPath("$.mensagem").value("Cupom válido"));

            verify(cupomService).validarCupom("PROMO10");
        }

        @Test
        @DisplayName("deve retornar 200 com valido=false quando cupom expirado ou inexistente")
        void deveRetornar200ComValidoFalseQuandoCupomInvalido() throws Exception {
            // Arrange
            ValidacaoCupomResponse validacaoResponse = new ValidacaoCupomResponse(
                    false,
                    null,
                    null,
                    "Cupom expirado ou inválido"
            );
            when(cupomService.validarCupom("INVALIDO")).thenReturn(validacaoResponse);

            // Act & Assert
            mockMvc.perform(get("/api/cupons/validar/INVALIDO"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valido").value(false))
                    .andExpect(jsonPath("$.codigo").doesNotExist())
                    .andExpect(jsonPath("$.mensagem").value("Cupom expirado ou inválido"));

            verify(cupomService).validarCupom("INVALIDO");
        }
    }
}