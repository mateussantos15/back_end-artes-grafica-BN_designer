package com.bndesigner.controller.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bndesigner.dto.request.usuario.UsuarioCreateRequest;
import com.bndesigner.dto.request.usuario.UsuarioUpdateRequest;
import com.bndesigner.dto.response.usuario.UsuarioResponse;
import com.bndesigner.service.usuario.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
	
	private final UsuarioService usuarioService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioResponse criar(@RequestBody @Valid UsuarioCreateRequest request) {
	    return usuarioService.criar(request);
	}
	
	@GetMapping("/{id}")
	public UsuarioResponse buscar(@PathVariable Long id) {
	    return usuarioService.buscarPorId(id);
	}
	
	@GetMapping
	public Page<UsuarioResponse> listar(Pageable pageable) {
	    return usuarioService.listarTodos(pageable);
	}
	
	@PutMapping("/{id}")
	public UsuarioResponse atualizar(
	        @PathVariable Long id,
	        @RequestBody @Valid UsuarioUpdateRequest request
	) {
	    return usuarioService.atualizar(id, request);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
	    usuarioService.remover(id);
	}
}
