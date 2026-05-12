package com.bndesigner.repository.produto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bndesigner.domain.entity.produto.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
	
    boolean existsByTituloIgnoreCase(String titulo);
    
    @EntityGraph(attributePaths = {"categoria", "arquivo"})
    Page<Produto> findAll(Pageable pageable);
    
    @EntityGraph(attributePaths = {"categoria", "arquivo"})
    Page<Produto> findByCategoria_IdCategoria(Long idCategoria, Pageable pageable);


}
