package com.bndesigner.service.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bndesigner.dto.request.usuario.UsuarioCreateRequest;
import com.bndesigner.dto.request.usuario.UsuarioUpdateRequest;
import com.bndesigner.dto.response.usuario.UsuarioResponse;


public interface UsuarioService {

	UsuarioResponse criar(UsuarioCreateRequest request);
	
	UsuarioResponse buscarPorId(Long id);
	
	Page<UsuarioResponse> listar(Pageable pageable);

	UsuarioResponse atualizar(Long id, UsuarioUpdateRequest usuarioAtualizado);
	
	void deletar(Long id);
}
