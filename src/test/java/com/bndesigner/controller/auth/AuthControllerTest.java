
package com.bndesigner.controller.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bndesigner.config.PasswordConfig;
import com.bndesigner.config.SecurityConfig;
import com.bndesigner.dto.request.login.LoginRequest;
import com.bndesigner.dto.response.login.LoginResponse;
import com.bndesigner.service.security.AuthenticationService;
import com.bndesigner.service.security.UsuarioDetailsService;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, PasswordConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveRealizarLoginComSucesso() throws Exception {

        given(authenticationService.autenticar(any(LoginRequest.class)))
                .willReturn(new LoginResponse("Login realizado com sucesso"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "admin.teste@exemplo.com",
                            "senha": "Admin123@"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem")
                        .value("Login realizado com sucesso"));
    }

    @Test
    void deveRetornar400QuandoEmailForInvalido() throws Exception {

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "email-invalido",
                            "senha": "Admin123@"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoSenhaEstiverVazia() throws Exception {

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "admin.teste@exemplo.com",
                            "senha": ""
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar401QuandoCredenciaisForemInvalidas() throws Exception {

        given(authenticationService.autenticar(any(LoginRequest.class)))
                .willThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "admin.teste@exemplo.com",
                            "senha": "senhaErrada"
                        }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error")
                        .value("Falha na autenticação"))
                .andExpect(jsonPath("$.message")
                        .value("E-mail ou senha inválidos"));
    }
}
