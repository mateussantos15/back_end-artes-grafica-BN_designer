package com.bndesigner.controller.cupom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bndesigner.dto.request.cupom.CupomCreateRequest;
import com.bndesigner.dto.request.cupom.CupomUpdateRequest;
import com.bndesigner.dto.response.cupom.CupomResponse;
import com.bndesigner.dto.response.cupom.ValidacaoCupomResponse;
import com.bndesigner.service.cupom.CupomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cupons")
@RequiredArgsConstructor
public class CupomController {
	
	private final CupomService cupomService;
	
	@PostMapping
	public CupomResponse criar(
			@Valid @RequestBody CupomCreateRequest createRequest) {
		
		return cupomService.criar(createRequest);		
	}
	
	@GetMapping
	public Page<CupomResponse> listar(Pageable pageable) {
		
		return cupomService.listar(pageable);
	}
	
	@GetMapping("/{id}")
	public CupomResponse buscarPorId(@PathVariable Long id) {
		
		return cupomService.buscarPorId(id);
	}
	
	@PutMapping("/{id}")
	public CupomResponse atualizar(
			@PathVariable Long id,
			@Valid @RequestBody CupomUpdateRequest updateRequest) {
		
		return cupomService.atualizar(id, updateRequest);
	}
	
	@DeleteMapping("/{id}")
	public void deletar(@PathVariable Long id) {
		
		cupomService.deletar(id);
	}
	
    // ───────────────────────────────────────
    // ENDPOINT ESPECIAL
    // ───────────────────────────────────────

	@GetMapping("/validar/{codigo}")
    public ValidacaoCupomResponse validarCupom(@PathVariable String codigo) {

        return cupomService.validarCupom(codigo);
    }

}
