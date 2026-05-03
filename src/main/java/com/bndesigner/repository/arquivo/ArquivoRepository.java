package com.bndesigner.repository.arquivo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bndesigner.domain.entity.arquivo.Arquivo;

public interface ArquivoRepository extends JpaRepository<Arquivo, Long>{
	
	List<Arquivo> findByAtivoTrue();
	
	Optional<Arquivo> findByHashArquivo(String hashArquivo);
	
	boolean existsByHashArquivo(String hashArquivo);

}
