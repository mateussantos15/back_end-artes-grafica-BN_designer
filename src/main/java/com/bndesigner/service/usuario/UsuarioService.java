package com.bndesigner.service.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bndesigner.dto.request.usuario.UsuarioCreateRequest;
import com.bndesigner.dto.request.usuario.UsuarioUpdateRequest;
import com.bndesigner.dto.response.usuario.UsuarioResponse;


public interface UsuarioService {

	public UsuarioResponse criar(UsuarioCreateRequest request);
	
	public UsuarioResponse buscarPorId(Long id);
	
	public Page<UsuarioResponse> listarTodos(Pageable pageable);

	public UsuarioResponse atualizar(Long id, UsuarioUpdateRequest usuarioAtualizado);
	
	public void deletar(Long id);
}
