package com.bndesigner.service.cupom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bndesigner.dto.request.copum.CupomCreateRequest;
import com.bndesigner.dto.request.copum.CupomUpdateRequest;
import com.bndesigner.dto.response.cupom.CupomResponse;

public interface CupomService {
	
	CupomResponse criar(CupomCreateRequest createRequest);
	
	CupomResponse buscarPorId(Long id);
	
	Page<CupomResponse> listar(Long id, Pageable pageable);
	
	CupomResponse atualizar(Long id, CupomUpdateRequest updateRequest);
	
	void deletar(Long id);

}
