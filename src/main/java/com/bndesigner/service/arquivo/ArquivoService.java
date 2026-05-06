package com.bndesigner.service.arquivo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bndesigner.dto.request.arquivo.ArquivoCreateRequest;
import com.bndesigner.dto.request.arquivo.ArquivoUpdateRequest;
import com.bndesigner.dto.response.arquivo.ArquivoResponse;

public interface ArquivoService {
	
	ArquivoResponse criar(ArquivoCreateRequest creatRequest);
	
	ArquivoResponse buscarPorId(Long id);
	
	Page<ArquivoResponse> listarAtivos(Pageable pageable);
	
	ArquivoResponse atualizar(Long id, ArquivoUpdateRequest updateRequest);
	
	void desativar(Long id);

}
