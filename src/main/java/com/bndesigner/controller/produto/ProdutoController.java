package com.bndesigner.controller.produto;

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

import com.bndesigner.dto.request.produto.ProdutoCreateRequest;
import com.bndesigner.dto.request.produto.ProdutoUpdateRequest;
import com.bndesigner.dto.response.produto.ProdutoResponse;
import com.bndesigner.service.produto.ProdutoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {
	
	private final ProdutoService produtoService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProdutoResponse criar(
			@Valid @RequestBody ProdutoCreateRequest createRequest) {
		return produtoService.criar(createRequest);
	}
	
	@GetMapping("/{id}")
	public ProdutoResponse buscarPorId(@PathVariable Long id) {
		return produtoService.buscarPorId(id);
	}
	
	@GetMapping
	public Page<ProdutoResponse> listar(Pageable pageable) {
		return produtoService.listar(pageable);
	}
	
	@GetMapping("/categoria/{idCategoria}")
	public Page<ProdutoResponse> listarPorCategoria(
			@PathVariable Long idCategoria, 
			Pageable pageable) {
		return produtoService.listarPorCategoria(idCategoria, pageable);
	}
	
	@PutMapping("/{id}")
	public ProdutoResponse atualizar(@PathVariable Long id,
			@Valid @RequestBody ProdutoUpdateRequest updateRequest) {
		return produtoService.atualizar(id, updateRequest);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletar(@PathVariable Long id) {
		produtoService.deletar(id);
	}

}
