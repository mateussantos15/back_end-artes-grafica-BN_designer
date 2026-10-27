package com.bndesigner.controller.avaliacao;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.bndesigner.dto.request.avaliacao.AvaliacaoCreateRequest;
import com.bndesigner.dto.request.avaliacao.AvaliacaoUpdateRequest;
import com.bndesigner.dto.response.avaliacao.AvaliacaoResponse;
import com.bndesigner.service.avaliacao.AvaliacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AvaliacaoController.class)
class AvaliacaoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AvaliacaoService avaliacaoService;

	@Nested
	@DisplayName("POST /api/avaliacoes")
	class Post {

		@Test
		@DisplayName("Deve criar uma avaliação com sucesso")
		void deveCriarAvaliacao() throws Exception {

			Long usuarioId = 1L;

			AvaliacaoCreateRequest request = new AvaliacaoCreateRequest(10L, 5, "Excelente produto.");

			AvaliacaoResponse response = new AvaliacaoResponse(100L, 10L, usuarioId, 5, "Excelente produto.", null);

			when(avaliacaoService.criar(eq(usuarioId), any(AvaliacaoCreateRequest.class))).thenReturn(response);

			mockMvc.perform(post("/api/avaliacoes").param("usuarioId", usuarioId.toString())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.idAvaliacao").value(100))
					.andExpect(jsonPath("$.produtoId").value(10))
					.andExpect(jsonPath("$.usuarioId").value(1))
					.andExpect(jsonPath("$.nota").value(5))
					.andExpect(jsonPath("$.comentario").value("Excelente produto."));

			verify(avaliacaoService).criar(eq(usuarioId), any(AvaliacaoCreateRequest.class));
		}
		
		@Test
		@DisplayName("Deve retornar 400 quando produtoId não for informado")
		void deveRetornar400QuandoProdutoIdNaoForInformado() throws Exception {

		    String json = """
		            {
		                "nota": 5,
		                "comentario": "Excelente produto."
		            }
		            """;

		    mockMvc.perform(post("/api/avaliacoes")
		            .param("usuarioId", "1")
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(json))
		            .andExpect(status().isBadRequest());

		    verifyNoInteractions(avaliacaoService);
		}
		
		@Test
		@DisplayName("Deve retornar 400 quando nota for menor que 1")
		void deveRetornar400QuandoNotaForMenorQueUm() throws Exception {

		    String json = """
		            {
		                "produtoId": 10,
		                "nota": 0,
		                "comentario": "Avaliação inválida."
		            }
		            """;

		    mockMvc.perform(post("/api/avaliacoes")
		            .param("usuarioId", "1")
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(json))
		            .andExpect(status().isBadRequest());

		    verifyNoInteractions(avaliacaoService);
		}
		
		@Test
		@DisplayName("Deve retornar 400 quando nota for maior que 5")
		void deveRetornar400QuandoNotaForMaiorQueCinco() throws Exception {

		    String json = """
		            {
		                "produtoId": 10,
		                "nota": 6,
		                "comentario": "Avaliação inválida."
		            }
		            """;

		    mockMvc.perform(post("/api/avaliacoes")
		            .param("usuarioId", "1")
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(json))
		            .andExpect(status().isBadRequest());

		    verifyNoInteractions(avaliacaoService);
		}
		
		@Test
		@DisplayName("Deve retornar 400 quando comentario exceder 1000 caracteres")
		void deveRetornar400QuandoComentarioExcederMilCaracteres()
		        throws Exception {

		    String comentario = "a".repeat(1001);

		    String json = """
		            {
		                "produtoId": 10,
		                "nota": 5,
		                "comentario": "%s"
		            }
		            """.formatted(comentario);

		    mockMvc.perform(post("/api/avaliacoes")
		            .param("usuarioId", "1")
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(json))
		            .andExpect(status().isBadRequest());

		    verifyNoInteractions(avaliacaoService);
		}
		
		@Test
		@DisplayName("Deve retornar 400 quando usuarioId não for informado")
		void deveRetornar400QuandoUsuarioIdNaoForInformado()
		        throws Exception {

		    String json = """
		            {
		                "produtoId": 10,
		                "nota": 5,
		                "comentario": "Excelente produto."
		            }
		            """;

		    mockMvc.perform(post("/api/avaliacoes")
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(json))
		            .andExpect(status().isBadRequest());

		    verifyNoInteractions(avaliacaoService);
		}
		
		@Test
		@DisplayName("Deve retornar 400 quando nota não for informada")
		void deveRetornar400QuandoNotaNaoForInformada()
		        throws Exception {

		    String json = """
		            {
		                "produtoId": 10,
		                "comentario": "Sem nota."
		            }
		            """;

		    mockMvc.perform(post("/api/avaliacoes")
		            .param("usuarioId", "1")
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(json))
		            .andExpect(status().isBadRequest());

		    verifyNoInteractions(avaliacaoService);
		}
		
		@Test
		@DisplayName("Deve retornar 400 ao atualizar com nota inválida")
		void deveRetornar400AoAtualizarComNotaInvalida()
		        throws Exception {

		    String json = """
		            {
		                "nota": 6,
		                "comentario": "Nota inválida."
		            }
		            """;

		    mockMvc.perform(put(
		            "/api/avaliacoes/{id}",
		            100L)
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(json))
		            .andExpect(status().isBadRequest());

		    verifyNoInteractions(avaliacaoService);
		}
		
		@Test
		@DisplayName("Deve retornar 400 ao atualizar sem nota")
		void deveRetornar400AoAtualizarSemNota()
		        throws Exception {

		    String json = """
		            {
		                "comentario": "Sem nota."
		            }
		            """;

		    mockMvc.perform(put(
		            "/api/avaliacoes/{id}",
		            100L)
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(json))
		            .andExpect(status().isBadRequest());

		    verifyNoInteractions(avaliacaoService);
		}
	}
	
	@Nested
	@DisplayName("GET /api/avaliacoes/")
	class Get {
		
		@Test
		@DisplayName("Deve buscar por ID")
		void deveBuscarAvaliacaoPorId() throws Exception {

		    Long idAvaliacao = 100L;

		    AvaliacaoResponse response =
		            new AvaliacaoResponse(
		                    idAvaliacao,
		                    10L,
		                    1L,
		                    5,
		                    "Excelente produto.",
		                    null);

		    when(avaliacaoService.buscarPorId(idAvaliacao))
		            .thenReturn(response);

		    mockMvc.perform(get("/api/avaliacoes/{id}", idAvaliacao))
		            .andExpect(status().isOk())
		            .andExpect(jsonPath("$.idAvaliacao").value(100))
		            .andExpect(jsonPath("$.produtoId").value(10))
		            .andExpect(jsonPath("$.usuarioId").value(1))
		            .andExpect(jsonPath("$.nota").value(5))
		            .andExpect(jsonPath("$.comentario")
		                    .value("Excelente produto."));

		    verify(avaliacaoService)
		            .buscarPorId(idAvaliacao);
		}
		
		@Test
		@DisplayName("Teste de listagem por produto")
		void deveListarAvaliacoesPorProduto() throws Exception {

		    Long produtoId = 10L;

		    AvaliacaoResponse response =
		            new AvaliacaoResponse(
		                    100L,
		                    produtoId,
		                    1L,
		                    5,
		                    "Excelente produto.",
		                    null);

		    Page<AvaliacaoResponse> pagina =
		            new PageImpl<>(
		                    List.of(response),
		                    PageRequest.of(0, 10),
		                    1);

		    when(avaliacaoService.listarPorProduto(
		            eq(produtoId),
		            any(Pageable.class)))
		            .thenReturn(pagina);

		    mockMvc.perform(get(
		            "/api/avaliacoes/produto/{produtoId}",
		            produtoId)
		            .param("page", "0")
		            .param("size", "10"))
		            .andExpect(status().isOk())
		            .andExpect(jsonPath("$.content.length()").value(1))
		            .andExpect(jsonPath("$.content[0].idAvaliacao")
		                    .value(100))
		            .andExpect(jsonPath("$.content[0].produtoId")
		                    .value(10))
		            .andExpect(jsonPath("$.content[0].usuarioId")
		                    .value(1))
		            .andExpect(jsonPath("$.content[0].nota")
		                    .value(5));

		    verify(avaliacaoService)
		            .listarPorProduto(
		                    eq(produtoId),
		                    any(Pageable.class));
		}
	}
	
	@Test
	@DisplayName("Teste de listagem por usuário")
	void deveListarAvaliacoesPorUsuario() throws Exception {

	    Long usuarioId = 1L;

	    AvaliacaoResponse response =
	            new AvaliacaoResponse(
	                    100L,
	                    10L,
	                    usuarioId,
	                    5,
	                    "Excelente produto.",
	                    null);

	    Page<AvaliacaoResponse> pagina =
	            new PageImpl<>(
	                    List.of(response),
	                    PageRequest.of(0, 10),
	                    1);

	    when(avaliacaoService.listarPorUsuario(
	            eq(usuarioId),
	            any(Pageable.class)))
	            .thenReturn(pagina);

	    mockMvc.perform(get(
	            "/api/avaliacoes/usuario/{usuarioId}",
	            usuarioId)
	            .param("page", "0")
	            .param("size", "10"))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.content.length()").value(1))
	            .andExpect(jsonPath("$.content[0].idAvaliacao")
	                    .value(100))
	            .andExpect(jsonPath("$.content[0].produtoId")
	                    .value(10))
	            .andExpect(jsonPath("$.content[0].usuarioId")
	                    .value(1))
	            .andExpect(jsonPath("$.content[0].nota")
	                    .value(5));

	    verify(avaliacaoService)
	            .listarPorUsuario(
	                    eq(usuarioId),
	                    any(Pageable.class));
		}
	
	
	@Nested
	@DisplayName("PUT /api/avaliacoes/{id}")
	class Put {
		
		@Test
		@DisplayName("Teste de atualização")
		void deveAtualizarAvaliacao() throws Exception {

		    Long avaliacaoId = 100L;

		    AvaliacaoUpdateRequest request =
		            new AvaliacaoUpdateRequest(
		                    4,
		                    "Produto muito bom.");

		    AvaliacaoResponse response =
		            new AvaliacaoResponse(
		                    avaliacaoId,
		                    10L,
		                    1L,
		                    4,
		                    "Produto muito bom.",
		                    null);

		    when(avaliacaoService.atualizar(
		            eq(avaliacaoId),
		            any(AvaliacaoUpdateRequest.class)))
		            .thenReturn(response);

		    mockMvc.perform(put(
		            "/api/avaliacoes/{id}",
		            avaliacaoId)
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(objectMapper.writeValueAsString(request)))
		            .andExpect(status().isOk())
		            .andExpect(jsonPath("$.idAvaliacao")
		                    .value(100))
		            .andExpect(jsonPath("$.nota")
		                    .value(4))
		            .andExpect(jsonPath("$.comentario")
		                    .value("Produto muito bom."));

		    verify(avaliacaoService)
		            .atualizar(
		                    eq(avaliacaoId),
		                    any(AvaliacaoUpdateRequest.class));
		}

	}
}
