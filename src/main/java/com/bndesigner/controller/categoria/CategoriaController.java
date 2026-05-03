package com.bndesigner.controller.categoria;

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

import com.bndesigner.dto.request.categoria.CategoriaCreateRequest;
import com.bndesigner.dto.request.categoria.CategoriaUpdateRequest;
import com.bndesigner.dto.response.categoria.CategoriaResponse;
import com.bndesigner.service.categoria.CategoriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {
	
	private final CategoriaService categoriaService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategoriaResponse criar(
			@Valid @RequestBody CategoriaCreateRequest request) {
		
		return categoriaService.criar(request);
		
	}
	
	@GetMapping("/{id}")
	public CategoriaResponse buscarPorId (@PathVariable Long id) {
		
		return categoriaService.buscarPorId(id);
		
	}
	
	@GetMapping
	public Page<CategoriaResponse> listar (Pageable pageable) {
		
		return categoriaService.listar(pageable);
		
	}
	
	@PutMapping("/{id}")
	public CategoriaResponse atualizar(
			@PathVariable Long id,
			@RequestBody @Valid CategoriaUpdateRequest updateRequest) {
		
		return categoriaService.atualizar(id, updateRequest);
		
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletar (@PathVariable Long id) {
		categoriaService.deletar(id);
	}

}
