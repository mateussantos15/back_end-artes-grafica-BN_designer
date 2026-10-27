package com.bndesigner.controller.avaliacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bndesigner.dto.request.avaliacao.AvaliacaoCreateRequest;
import com.bndesigner.dto.request.avaliacao.AvaliacaoUpdateRequest;
import com.bndesigner.dto.response.avaliacao.AvaliacaoResponse;
import com.bndesigner.service.avaliacao.AvaliacaoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {
	
	private final AvaliacaoService avaliacaoService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public AvaliacaoResponse criar(
			@RequestParam Long usuarioId,
			@Valid @RequestBody AvaliacaoCreateRequest createRequest
			
			) {
		return avaliacaoService.criar(usuarioId, createRequest);
	}
	
	@GetMapping("/{id}")
	public AvaliacaoResponse buscarPorId(@PathVariable("id") Long idAvaliacao) {
		return avaliacaoService.buscarPorId(idAvaliacao);
	}
	
	@GetMapping("/produto/{produtoId}")
	public Page<AvaliacaoResponse> listarPorProdutos(
			@PathVariable Long produtoId,
			Pageable pageable
			) {
		
		return avaliacaoService.listarPorProduto(produtoId, pageable);
	}
	
	@GetMapping("/usuario/{usuarioId}")
	public Page<AvaliacaoResponse> listarPorUsuario(
			@PathVariable Long usuarioId,
			Pageable pageable) {
		
		return avaliacaoService.listarPorUsuario(usuarioId, pageable);
	}
	
	@PutMapping("/{id}")
	public AvaliacaoResponse atualizar(
			@PathVariable Long id,
			@Valid @RequestBody AvaliacaoUpdateRequest updateRequest) {
		
		return avaliacaoService.atualizar(id, updateRequest);
	}

}
