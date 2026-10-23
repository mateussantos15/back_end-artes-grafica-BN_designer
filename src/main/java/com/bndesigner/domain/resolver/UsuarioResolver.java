package com.bndesigner.domain.resolver;

import org.springframework.stereotype.Component;

import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.repository.usuario.UsuarioRepository;
import com.bndesigner.util.EntityLookup;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioResolver {
	
	private final UsuarioRepository usuarioRepository;
	
	public Usuario buscarUsuario(Long usuarioId) {
		
		return EntityLookup.buscarOuLancar(
				usuarioRepository, 
				usuarioId, 
				"Usuário");
	}

}
