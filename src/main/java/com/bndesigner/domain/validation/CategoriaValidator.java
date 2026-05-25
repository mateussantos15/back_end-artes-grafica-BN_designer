package com.bndesigner.domain.validation;

import org.springframework.stereotype.Component;

import com.bndesigner.exceptions.custom.DuplicateResourceException;
import com.bndesigner.repository.categoria.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoriaValidator {
	
	private final CategoriaRepository categoriaRepository;
	
	public void validarNomeCategoria(String nome, Long id) {
		
		
		if (id != null) {
			
			boolean ehOMesmoNome = categoriaRepository.findById(id)
					.map(c -> c.getNome().equalsIgnoreCase(nome))
					.orElse(false);
			
			if (ehOMesmoNome) return;
			
		}
		
		boolean nomeEmUso = categoriaRepository.existsByNome(nome);
		
		if (nomeEmUso) {
			
			throw new DuplicateResourceException("Categoria", "Nome", nome);
		}			
	}
}
