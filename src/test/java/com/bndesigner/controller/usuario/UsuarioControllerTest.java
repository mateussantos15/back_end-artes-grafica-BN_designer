package com.bndesigner.controller.usuario;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bndesigner.dto.request.usuario.UsuarioCreateRequest;
import com.bndesigner.dto.request.usuario.UsuarioUpdateRequest;
import com.bndesigner.dto.response.usuario.UsuarioResponse;
import com.bndesigner.service.usuario.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarUsuarioComSucesso() throws Exception {
        UsuarioCreateRequest request =
                new UsuarioCreateRequest("Mateus"," ", "mateus@email.com", "123");

        UsuarioResponse response =
                new UsuarioResponse(1L, "Mateus", " ", "mateus@email.com");

        when(usuarioService.criar(any()))
                .thenReturn(response);

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.idUsuario").value(1L))
            .andExpect(jsonPath("$.email").value("mateus@email.com"));
    }

    @Test
    void deveBuscarUsuarioPorIdComSucesso() throws Exception {
        UsuarioResponse response =
                new UsuarioResponse(1L, "Mateus", " ", "mateus@email.com");

        when(usuarioService.buscarPorId(1L))
                .thenReturn(response);

        mockMvc.perform(get("/usuarios/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("Mateus"));
    }

    @Test
    void deveListarUsuariosComSucesso() throws Exception {
        UsuarioResponse response =
                new UsuarioResponse(1L, "Mateus", " ", "mateus@email.com");

        Page<UsuarioResponse> page =
                new PageImpl<>(List.of(response));

        when(usuarioService.listarTodos(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/usuarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].email")
                .value("mateus@email.com"));
    }

    @Test
    void deveAtualizarUsuarioComSucesso() throws Exception {
        UsuarioUpdateRequest request =
                new UsuarioUpdateRequest("Mateus Atualizado", "Atualizado", "novo@email.com");

        UsuarioResponse response =
                new UsuarioResponse(1L, "Mateus Atualizado", "Atualizado", "novo@email.com");

        when(usuarioService.atualizar(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(put("/usuarios/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("novo@email.com"));
    }

    @Test
    void deveRemoverUsuarioComSucesso() throws Exception {
        mockMvc.perform(delete("/usuarios/{id}", 1L))
            .andExpect(status().isNoContent());
    }
}