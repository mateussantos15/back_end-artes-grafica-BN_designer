package com.bndesigner.repository.arquivo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bndesigner.domain.entity.arquivo.Arquivo;

public interface ArquivoRepository extends JpaRepository<Arquivo, Long>{
	
	Page<Arquivo> findByAtivoTrue(Pageable pageable);
	
	Optional<Arquivo> findByHashArquivo(String hashArquivo);
	
	boolean existsByHashArquivo(String hashArquivo);

}
