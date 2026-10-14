package com.bndesigner.repository.avaliacao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bndesigner.domain.entity.avaliacao.Avaliacao;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
	
	 Page<Avaliacao> findByProdutoIdProduto(Long produtoId, Pageable pageable);

	 Page<Avaliacao> findByUsuarioIdUsuario(Long usuarioId, Pageable pageable);

	 Optional<Avaliacao> findByUsuarioIdUsuarioAndProdutoIdProduto(
	            Long usuarioId,
	            Long produtoId);

	    boolean existsByUsuarioIdUsuarioAndProdutoIdProduto(
	            Long usuarioId,
	            Long produtoId);

}
