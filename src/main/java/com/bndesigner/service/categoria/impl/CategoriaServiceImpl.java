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
		
		validarNomeCategoria(request.nome(), null);
		
		Categoria categoria = categoriaMapper.toEntity(request);
		Categoria salvo = categoriaRepository.save(categoria);
		
		return categoriaMapper.toResponse(salvo);
	}

	
	@Override
	@Transactional(readOnly = true)
	public CategoriaResponse buscarPorId(Long id) {
		
		Categoria categoria = buscarEntidadePorId(id);
		
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
		
		Categoria categoria = buscarEntidadePorId(id);
		
		validarNomeCategoria(categoriaAtualizada.nome(), id);
		
		categoriaMapper.updateEntityFromRequest(categoriaAtualizada, categoria);
		Categoria atualizado = categoriaRepository.save(categoria);
		
		return categoriaMapper.toResponse(atualizado);
	}

	@Override
	public void deletar(Long id) {
		
		Categoria categoria = buscarEntidadePorId(id);
		categoriaRepository.delete(categoria);
		
	}
	
	
	
	private Categoria buscarEntidadePorId(Long id) {
		
		return categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Categoria", id));	
	}
	
	
	private void validarNomeCategoria(String nome, Long id) {
		
		
		if (id != null) {
			
			boolean ehOMesmoNome = categoriaRepository.findById(id)
					.map(c -> c.getNome().equalsIgnoreCase(nome))
					.orElse(false);
			
			if (ehOMesmoNome) return;
			
		}
		
		boolean nomeEmUso = categoriaRepository.existsByNome(nome);
		
		if (nomeEmUso) {
			
			throw new BusinessException(
					HttpStatus.CONFLICT,
					"Conflito de Dados",
					String.format("Já existe uma Categoria com o nome: '%s'.", nome)
					);
		}			
	}
}
