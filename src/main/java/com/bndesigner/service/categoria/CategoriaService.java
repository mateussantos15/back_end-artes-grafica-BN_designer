package com.bndesigner.service.categoria;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bndesigner.dto.request.categoria.CategoriaCreateRequest;
import com.bndesigner.dto.request.categoria.CategoriaUpdateRequest;
import com.bndesigner.dto.response.categoria.CategoriaResponse;

public interface CategoriaService {
	
	CategoriaResponse criar(CategoriaCreateRequest request);
	
	CategoriaResponse buscarPorId(Long id);
	
	Page<CategoriaResponse> listar(Pageable pageable);
	
	CategoriaResponse atualizar(Long id, CategoriaUpdateRequest request);
	
	void deletar(Long id);

}
