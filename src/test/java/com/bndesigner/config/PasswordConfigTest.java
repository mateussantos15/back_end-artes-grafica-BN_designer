package com.bndesigner.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordConfigTest {
	
	private final PasswordEncoder passwordEncoder = 
			new PasswordConfig().passwordEncoder();
	
	@Test
	@DisplayName("Deve codificar a senha corretamente")
	void deveCodificarSenha() {
		
		String senha = "minhaSenha123";
		
		String senhaCodificada = passwordEncoder.encode(senha);
		
		assertNotEquals(senha, senhaCodificada);
		
	}
	
	@Test
	@DisplayName("Deve validar a senha corretamente")
	void deveValidarSenha() {
		
		String senha = "minhaSenha123";
		
		String senhaCodificada = passwordEncoder.encode(senha);
		
		assertTrue(passwordEncoder.matches(senha, senhaCodificada));
	}
	
	@Test
	@DisplayName("Deve retornar false para senha incorreta")
	void naoDeveValidarSenhaIncorreta() {
		
		String senha = "minhaSenha123";
		
		String senhaCodificada = passwordEncoder.encode(senha);
		
		assertFalse(passwordEncoder.matches("outraSenha", senhaCodificada));
	}

}
