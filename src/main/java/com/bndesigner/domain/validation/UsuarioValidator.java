package com.bndesigner.domain.validation;

import org.springframework.stereotype.Component;

import com.bndesigner.exceptions.custom.DuplicateResourceException;
import com.bndesigner.repository.usuario.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioValidator {
	
	private final UsuarioRepository usuarioRepository;
	
	public void validarEmail(Long id, String email) {
		
		boolean emailEmUso = usuarioRepository.existsByEmail(email);
		
		if (emailEmUso) {
			boolean ehOMesmoEmail = id != null
					&& usuarioRepository.findByEmail(email)
					.map(u -> u.getIdUsuario().equals(id))
					.orElse(false);
			
			if (!ehOMesmoEmail) {
				throw new DuplicateResourceException("Usuário", "Email", email);
			}
		}
	}
}
