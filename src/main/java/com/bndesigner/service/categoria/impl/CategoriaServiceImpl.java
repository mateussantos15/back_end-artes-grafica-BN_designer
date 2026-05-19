package com.bndesigner.service.categoria.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bndesigner.domain.entity.categoria.Categoria;
import com.bndesigner.dto.request.categoria.CategoriaCreateRequest;
import com.bndesigner.dto.request.categoria.CategoriaUpdateRequest;
import com.bndesigner.dto.response.categoria.CategoriaResponse;
import com.bndesigner.mapper.categoria.CategoriaMapper;
import com.bndesigner.repository.categoria.CategoriaRepository;
import com.bndesigner.service.categoria.CategoriaService;
import com.bndesigner.service.validation.CategoriaValidator;
import com.bndesigner.util.EntityLookup;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {
	
	private final CategoriaRepository categoriaRepository;
	private final CategoriaMapper categoriaMapper;
	private final CategoriaValidator categoriaValidator;

	
	@Override
	@Transactional
	public CategoriaResponse criar(CategoriaCreateRequest request) {
		
		categoriaValidator.validarNomeCategoria(request.nome(), null);;
		
		Categoria categoria = categoriaMapper.toEntity(request);
		Categoria salvo = categoriaRepository.save(categoria);
		
		return categoriaMapper.toResponse(salvo);
	}

	
	@Override
	@Transactional(readOnly = true)
	public CategoriaResponse buscarPorId(Long id) {
		
		Categoria categoria = EntityLookup.buscarOuLancar(categoriaRepository, id, "Categoria");
		
		return categoriaMapper.toResponse(categoria);
	}

	
	@Override
	@Transactional(readOnly = true)
	public Page<CategoriaResponse> listar(Pageable pageable) {
		
		return categoriaRepository.findAll(pageable)
				.map(categoriaMapper::toResponse);
	}

	@Override
	public CategoriaResponse atualizar(Long id, CategoriaUpdateRequest categoriaAtualizada) {
		
		Categoria categoria = EntityLookup.buscarOuLancar(categoriaRepository, id, "Categoria");
		
		categoriaValidator.validarNomeCategoria(categoriaAtualizada.nome(), id);
		
		categoriaMapper.updateEntityFromRequest(categoriaAtualizada, categoria);
		Categoria atualizado = categoriaRepository.save(categoria);
		
		return categoriaMapper.toResponse(atualizado);
	}

	@Override
	public void deletar(Long id) {
		
		Categoria categoria = EntityLookup.buscarOuLancar(
				categoriaRepository, id, "Categoria");
		
		categoriaRepository.delete(categoria);
		
	}
}
