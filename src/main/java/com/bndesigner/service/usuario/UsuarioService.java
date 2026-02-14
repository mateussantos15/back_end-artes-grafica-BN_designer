package com.bndesigner.service.usuario;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.dto.request.usuario.UsuarioCreateRequest;
import com.bndesigner.dto.request.usuario.UsuarioUpdateRequest;
import com.bndesigner.dto.response.usuario.UsuarioResponse;
import com.bndesigner.mapper.usuario.UsuarioMapper;
import com.bndesigner.repository.usuario.UsuarioRepository;
import com.bndesigner.service.exception.usuario.EmailJaCadastradoException;
import com.bndesigner.service.exception.usuario.UsuarioNaoEncontradoException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
	
	private final UsuarioRepository usuarioRepository;
	private final UsuarioMapper usuarioMapper;
	
	@Transactional
	public UsuarioResponse criar(UsuarioCreateRequest request) {
		if (usuarioRepository.existsByEmail(request.emai())) {
			throw new EmailJaCadastradoException(request.emai());
		}
		
		Usuario usuario = usuarioMapper.toEntity(request);
		Usuario salvo = usuarioRepository.save(usuario);
		
		return usuarioMapper.toResponse(salvo);
	}
	
	@Transactional(readOnly = true)
	public UsuarioResponse buscarPorId(Long id) {
		
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new UsuarioNaoEncontradoException (id));
		
		return usuarioMapper.toResponse(usuario);
	}
	
	@Transactional(readOnly = true)
	public Page<UsuarioResponse> listarTodos(Pageable pageable) {
		return usuarioRepository.findAll(pageable)
				.map(usuarioMapper::toResponse);
	}
	
	@Transactional
	public UsuarioResponse atualizar(Long id, UsuarioUpdateRequest usuarioAtualizado) {
		
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new UsuarioNaoEncontradoException(id));
		
		if (!usuario.getEmail().equals(usuarioAtualizado.email())
	            && usuarioRepository.existsByEmail(usuarioAtualizado.email())) {
	        throw new EmailJaCadastradoException(usuarioAtualizado.email());
	    }
		
		usuarioMapper.updateEntityFromRequest(usuarioAtualizado, usuario);
		
		Usuario salvo = usuarioRepository.save(usuario);
		return usuarioMapper.toResponse(salvo);
	}
	
	@Transactional
	public void remover(Long id) {
	    Usuario usuario = usuarioRepository.findById(id)
	            .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

	    usuarioRepository.delete(usuario);
	}
}
