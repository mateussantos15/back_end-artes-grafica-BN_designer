package com.bndesigner.service.arquivo.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bndesigner.domain.entity.arquivo.Arquivo;
import com.bndesigner.dto.request.arquivo.ArquivoCreateRequest;
import com.bndesigner.dto.request.arquivo.ArquivoUpdateRequest;
import com.bndesigner.dto.response.arquivo.ArquivoResponse;
import com.bndesigner.exceptions.custom.BusinessException;
import com.bndesigner.exceptions.custom.ResourceNotFoundException;
import com.bndesigner.mapper.arquivo.ArquivoMapper;
import com.bndesigner.repository.arquivo.ArquivoRepository;
import com.bndesigner.service.arquivo.ArquivoService;
import com.bndesigner.util.EntityLookup;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class ArquivoServiceImpl implements ArquivoService {
	
	private final ArquivoRepository arquivoRepository;
	private final ArquivoMapper arquivoMapper;

	@Override
	public ArquivoResponse criar(ArquivoCreateRequest creatRequest) {
		
		if(creatRequest.hashArquivo() != null &&
				arquivoRepository.existsByHashArquivo(creatRequest.hashArquivo())) {
			
			throw new BusinessException(
					HttpStatus.CONFLICT, 
					"Hash já cadastrado!", 
					"Já axiste um arquivo com esse hash");
			
		}
		
		Arquivo entity = arquivoMapper.toEntity(creatRequest);
		
		return arquivoMapper.toResponse(arquivoRepository.save(entity));
	}

	@Override
	@Transactional(readOnly = true)
	public ArquivoResponse buscarPorId(Long id) {
		
		Arquivo entity = EntityLookup.buscarOuLancar(
				arquivoRepository, id, "Arquivo");
		
		return arquivoMapper.toResponse(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ArquivoResponse> listarAtivos(Pageable pageable) {
		
		return arquivoRepository.findByAtivoTrue(pageable)
				.map(arquivoMapper::toResponse);
	}

	@Override
	public ArquivoResponse atualizar(Long id, ArquivoUpdateRequest updateRequest) {
		
		Arquivo entity = EntityLookup.buscarOuLancar(
				arquivoRepository, id, "Arquivo");
		
		arquivoMapper.updateEntityFromRequest(updateRequest, entity);		
		
		return arquivoMapper.toResponse(arquivoRepository.save(entity));
	}

	@Override
	public void desativar(Long id) {
		
		Arquivo entity = EntityLookup.buscarOuLancar(
				arquivoRepository, id, "Arquivo");
		
		entity.setAtivo(false);
		
		arquivoRepository.save(entity);
		
	}
}
