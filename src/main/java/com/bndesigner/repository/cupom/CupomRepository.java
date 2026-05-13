package com.bndesigner.repository.cupom;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bndesigner.domain.entity.cupom.Cupom;
import com.bndesigner.domain.enums.cupom.StatusCupom;

public interface CupomRepository extends JpaRepository<Cupom, Long> {
	
	Optional<Cupom> findByCodigoIgnoreCase(String codigo);
	
	boolean existsByCodigoIgnoreCase(String codigo);
	
	Page<Cupom> findByStatus(StatusCupom status, Pageable pageable);

}
