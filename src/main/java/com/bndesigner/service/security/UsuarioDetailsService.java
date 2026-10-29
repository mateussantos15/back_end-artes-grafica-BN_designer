package com.bndesigner.service.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.domain.security.UsuarioDetails;
import com.bndesigner.repository.usuario.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {
	
	private final UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		
		Usuario usuario = usuarioRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException(
						"Usuário não encontrado com o email: " + username));
		
		return new UsuarioDetails(usuario);
	}
	

}
