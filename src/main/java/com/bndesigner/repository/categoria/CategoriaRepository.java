package com.bndesigner.repository.categoria;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bndesigner.domain.entity.categoria.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
	
	boolean existsByNome(String nome);

}
