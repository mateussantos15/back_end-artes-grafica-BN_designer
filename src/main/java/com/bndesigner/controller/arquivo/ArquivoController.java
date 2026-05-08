package com.bndesigner.controller.arquivo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bndesigner.dto.request.arquivo.ArquivoCreateRequest;
import com.bndesigner.dto.request.arquivo.ArquivoUpdateRequest;
import com.bndesigner.dto.response.arquivo.ArquivoResponse;
import com.bndesigner.service.arquivo.ArquivoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/arquivos")
@RequiredArgsConstructor
public class ArquivoController {
	
	private final ArquivoService arquivoService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ArquivoResponse criar(
			@Valid @RequestBody ArquivoCreateRequest createRequest) {
		
		return arquivoService.criar(createRequest);
	}
	
	@GetMapping("/{id}")
	public ArquivoResponse buscarPorId(@PathVariable Long id) {
		
		return arquivoService.buscarPorId(id);
		
	}
	
	@GetMapping
	public Page<ArquivoResponse> listarAtivos(Pageable pageable) {
		
		return arquivoService.listarAtivos(pageable);
		
	}
	
	@PutMapping("/{id}")
	public ArquivoResponse atualizar(
			@PathVariable Long id,
			@RequestBody @Valid ArquivoUpdateRequest updateRequest) {
		
		return arquivoService.atualizar(id, updateRequest);
		
	}
	
	@PatchMapping("/{id}/desativar")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void desativar(@PathVariable Long id) {
		
		arquivoService.desativar(id);
		
	}

}
