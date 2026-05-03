package com.bndesigner.service.categoria.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bndesigner.domain.entity.categoria.Categoria;
import com.bndesigner.dto.request.categoria.CategoriaCreateRequest;
import com.bndesigner.dto.request.categoria.CategoriaUpdateRequest;
import com.bndesigner.dto.response.categoria.CategoriaResponse;
import com.bndesigner.exceptions.BusinessException;
import com.bndesigner.exceptions.ResourceNotFoundException;
import com.bndesigner.mapper.categoria.CategoriaMapper;
import com.bndesigner.repository.categoria.CategoriaRepository;
import com.bndesigner.service.categoria.CategoriaService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {
	
	private final CategoriaRepository categoriaRepository;
	private final CategoriaMapper categoriaMapper;

	
	@Override
	@Transactional
	public CategoriaResponse criar(CategoriaCreateRequest request) {
		
		if (categoriaRepository.existsByNome(request.nome())) {
			throw new BusinessException(
					HttpStatus.CONFLICT,
					"Nome já cadastrado",
					"Já existe uma categoria com o nome: " + request.nome());
		}
		
		Categoria categoria = categoriaMapper.toEntity(request);
		Categoria salvo = categoriaRepository.save(categoria);
		
		return categoriaMapper.toResponse(salvo);
	}

	
	@Override
	@Transactional(readOnly = true)
	public CategoriaResponse buscarPorId(Long id) {
		
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(
						HttpStatus.NOT_FOUND,
						"Categoria não encontrada!",
						"Nenhuma categoria encontrada com o id: " +id));
		
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
		
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new BusinessException(
						HttpStatus.NOT_FOUND,
						"Categoria não encontrada!",
						"Nenhuma categoria encontrada com o id: " +id));
		
		if(!categoria.getNome().equals(categoriaAtualizada.nome())
				&& categoriaRepository.existsByNome(categoriaAtualizada.nome())) {
			throw new BusinessException(
					HttpStatus.CONFLICT,
					"Nome já cadastrado",
					"Já existe uma categoria com o nome: " + categoriaAtualizada.nome());
		}
		
		categoriaMapper.updateEntityFromRequest(categoriaAtualizada, categoria);
		Categoria atualizado = categoriaRepository.save(categoria);
		
		return categoriaMapper.toResponse(atualizado);
	}

	@Override
	public void deletar(Long id) {
		
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new BusinessException(
						HttpStatus.NOT_FOUND,
						"Categoria não encontrada!",
						"Nenhuma categoria encontrada com o id: " +id));
		
		categoriaRepository.delete(categoria);
		
	}

}
