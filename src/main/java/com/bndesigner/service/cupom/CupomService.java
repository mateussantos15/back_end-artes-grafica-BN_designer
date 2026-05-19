package com.bndesigner.service.cupom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bndesigner.dto.request.cupom.CupomCreateRequest;
import com.bndesigner.dto.request.cupom.CupomUpdateRequest;
import com.bndesigner.dto.response.cupom.CupomResponse;
import com.bndesigner.dto.response.cupom.ValidacaoCupomResponse;

public interface CupomService {
	
	CupomResponse criar(CupomCreateRequest createRequest);
	
	CupomResponse buscarPorId(Long id);
	
	Page<CupomResponse> listar(Pageable pageable);
	
	CupomResponse atualizar(Long id, CupomUpdateRequest updateRequest);
	
	void deletar(Long id);
	
	ValidacaoCupomResponse validarCupom(String codigo);

}
