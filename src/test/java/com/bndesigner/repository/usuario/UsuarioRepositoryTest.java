package com.bndesigner.repository.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.domain.enums.usuario.TipoUsuario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve salvar e buscar usuário por email")
    void deveSalvarEConsultarUsuarioPorEmail() {
        Usuario usuario = Usuario.builder()
        		.nome("Mateus")
        		.sobrenome("Santos")
        		.email("mateus@email.com")
        		.senha("hash_da_senha")
        		.tipoUsuario(TipoUsuario.CLIENTE)
        		.build();

        usuarioRepository.save(usuario);

        Optional<Usuario> resultado =
                usuarioRepository.findByEmail("mateus@email.com");

        assertTrue(resultado.isPresent());
        assertEquals("Mateus", resultado.get().getNome());
    }
    
    
    @Test
    void deveRetornarVazioQuandoEmailNaoExistir() {
        Optional<Usuario> resultado =
                usuarioRepository.findByEmail("inexistente@email.com");

        assertTrue(resultado.isEmpty());
    }
    

    @Test
    @DisplayName("Deve verificar existência de usuário por email")
    void deveRetornarTrueQuandoEmailExistir() {
    	 Usuario usuario = Usuario.builder()
         		.nome("Mateus")
         		.email("mateus@email.com")
         		.senha("hash_da_senha")
         		.tipoUsuario(TipoUsuario.CLIENTE)
         		.build();

        usuarioRepository.save(usuario);

        boolean existe = usuarioRepository.existsByEmail("mateus@email.com");

        assertThat(existe).isTrue();
    }
    
    
    @Test
    void deveRetornarFalseQuandoEmailNaoExistir() {
        boolean existe =
                usuarioRepository.existsByEmail("inexistente@email.com");

        assertFalse(existe);
    }
}
