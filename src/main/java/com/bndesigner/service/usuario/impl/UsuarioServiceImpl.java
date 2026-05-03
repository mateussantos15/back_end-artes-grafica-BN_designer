package com.bndesigner.service.usuario.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.dto.request.usuario.UsuarioCreateRequest;
import com.bndesigner.dto.request.usuario.UsuarioUpdateRequest;
import com.bndesigner.dto.response.usuario.UsuarioResponse;
import com.bndesigner.exceptions.BusinessException;
import com.bndesigner.exceptions.ResourceNotFoundException;

import com.bndesigner.mapper.usuario.UsuarioMapper;
import com.bndesigner.repository.usuario.UsuarioRepository;
import com.bndesigner.service.usuario.UsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService{
	
	private final UsuarioRepository usuarioRepository;
	private final UsuarioMapper usuarioMapper;

	@Override
	@Transactional
	public UsuarioResponse criar(UsuarioCreateRequest request) {
		
		if (usuarioRepository.existsByEmail(request.email())) {
			throw new BusinessException(
			        HttpStatus.CONFLICT,
			        "Email já cadastrado",
			        "O email " + request.email() + " já está em uso"
			    );
		}
		
		Usuario usuario = usuarioMapper.toEntity(request);
		Usuario salvo = usuarioRepository.save(usuario);
		
		return usuarioMapper.toResponse(salvo);
	}

	@Override
	@Transactional(readOnly = true)
	public UsuarioResponse buscarPorId(Long id) {
		
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(
					    HttpStatus.NOT_FOUND,
					    "Usuário não encontrado",
					    "Nenhum usuário encontrado com o id: " + id
					));
		
		return usuarioMapper.toResponse(usuario);

	}

	@Override
	@Transactional(readOnly = true)
	public Page<UsuarioResponse> listar(Pageable pageable) {
		
		return usuarioRepository.findAll(pageable)
				.map(usuarioMapper::toResponse);
	}

	@Override
	@Transactional
	public UsuarioResponse atualizar(Long id, UsuarioUpdateRequest usuarioAtualizado) {
		
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(
					    HttpStatus.NOT_FOUND,
					    "Usuário não encontrado",
					    "Nenhum usuário encontrado com o id: " + id
					));
		
		if (!usuario.getEmail().equals(usuarioAtualizado.email())
				&& usuarioRepository.existsByEmail(usuarioAtualizado.email())) {
			throw new BusinessException(
			        HttpStatus.CONFLICT,
			        "Email já cadastrado",
			        "O email " + usuarioAtualizado.email() + " já está em uso"
			    );
		}
		
		usuarioMapper.updateEntityFromRequest(usuarioAtualizado, usuario);
		
		Usuario salvo = usuarioRepository.save(usuario);
		return usuarioMapper.toResponse(salvo);
	}

	@Override
	@Transactional
	public void deletar(Long id) {
		
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(
					    HttpStatus.NOT_FOUND,
					    "Usuário não encontrado",
					    "Nenhum usuário encontrado com o id: " + id
					));
		
		usuarioRepository.delete(usuario);
		
	}

}
