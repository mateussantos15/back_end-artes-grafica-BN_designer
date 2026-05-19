package com.bndesigner.service.cupom.impl;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bndesigner.domain.entity.cupom.Cupom;
import com.bndesigner.domain.enums.cupom.StatusCupom;
import com.bndesigner.dto.request.cupom.CupomCreateRequest;
import com.bndesigner.dto.request.cupom.CupomUpdateRequest;
import com.bndesigner.dto.response.cupom.CupomResponse;
import com.bndesigner.dto.response.cupom.ValidacaoCupomResponse;
import com.bndesigner.exceptions.BusinessException;
import com.bndesigner.exceptions.ResourceNotFoundException;
import com.bndesigner.mapper.cupom.CupomMapper;
import com.bndesigner.repository.cupom.CupomRepository;
import com.bndesigner.service.cupom.CupomService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CupomServiceImpl implements CupomService {
	
	private final CupomRepository cupomRepository;
	private final CupomMapper cupomMapper;

	@Override
	public CupomResponse criar(CupomCreateRequest createRequest) {
		
		validarCodigoDuplicado(createRequest.codigo(), null);
		
		validarDataValidade(createRequest.dataValidade());
		
		Cupom cupom = cupomMapper.toEntity(createRequest);
		
		atualizarStatusAutomaticamente(cupom);
		
		Cupom salvo = cupomRepository.save(cupom);
		
		return cupomMapper.toResponse(salvo);
	}

	@Override
	@Transactional(readOnly = true)
	public CupomResponse buscarPorId(Long id) {
		
		Cupom cupom = buscarCupomPorId(id);
		
		atualizarStatusAutomaticamente(cupom);
		
		return cupomMapper.toResponse(cupom);
	}

	@Override
	@Transactional
	public Page<CupomResponse> listar(Pageable pageable) {
		
		Page<Cupom> cuponsPage = cupomRepository.findAll(pageable);
		
		return cuponsPage.map(cupom -> {
			this.atualizarStatusAutomaticamente(cupom);
			return cupomMapper.toResponse(cupom);
		});
	}

	@Override
	public CupomResponse atualizar(Long id, CupomUpdateRequest updateRequest) {
		
		Cupom cupom = buscarCupomPorId(id);
		
		validarCodigoDuplicado(updateRequest.codigo(), id);
		
		validarDataValidade(updateRequest.dataValidade());
		
		cupomMapper.updateEntityFromRequest(updateRequest, cupom);
		
		atualizarStatusAutomaticamente(cupom);
		
		Cupom atualizado = cupomRepository.save(cupom);
		
		return cupomMapper.toResponse(atualizado);
	}

	@Override
	public void deletar(Long id) {
		
		Cupom cupom = buscarCupomPorId(id);
		
		cupomRepository.delete(cupom);
		
	}
	
	@Override
	@Transactional(readOnly = true)
	public ValidacaoCupomResponse validarCupom(String codigo) {

	    Optional<Cupom> optionalCupom =
	            cupomRepository.findByCodigoIgnoreCase(codigo);

	    if (optionalCupom.isEmpty()) {

	        return new ValidacaoCupomResponse(
	                false,
	                codigo,
	                null,
	                "Cupom não encontrado"
	        );
	    }

	    Cupom cupom = optionalCupom.get();

	    atualizarStatusAutomaticamente(cupom);

	    if (cupom.getStatus() != StatusCupom.ATIVO) {

	        return new ValidacaoCupomResponse(
	                false,
	                codigo,
	                null,
	                "Cupom inválido ou expirado"
	        );
	    }

	    return new ValidacaoCupomResponse(
	            true,
	            cupom.getCodigo(),
	            cupom.getDescontoPercentual(),
	            "Cupom válido"
	    );
	}
	
	
	private Cupom buscarCupomPorId(Long id) {
		
		return cupomRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Cupom", id));
	}
	
	private void validarCodigoDuplicado(String codigo, Long id) {
		
		if(id != null) {
			
			boolean ehOMesmoCodigo = cupomRepository.findById(id)
					.map(c -> c.getCodigo().equalsIgnoreCase(codigo))
					.orElse(false);
			
			if(ehOMesmoCodigo) return;
		}
				
		if(cupomRepository.existsByCodigoIgnoreCase(codigo)) {
			
			throw new BusinessException(HttpStatus.CONFLICT,
					"Código de cupom já cadastrado",
					String.format("Já existe um cupom com esse código: '%s' ", codigo) );
			
		}
	}
	
	private void validarDataValidade(LocalDate dataValidade) {
		
		if(dataValidade.isBefore(LocalDate.now())) {
			
			throw new BusinessException(HttpStatus.BAD_REQUEST,
					"A validade do código expirou",
					String.format("A validade do cupom expirou em: '%s'.", dataValidade)
					);			
		}
	}
	
	private void atualizarStatusAutomaticamente(Cupom cupom) {
		
		if(cupom.getDataValidade().isBefore(LocalDate.now())) {
			
			cupom.setStatus(StatusCupom.EXPIRADO);
			
		} else if (cupom.getStatus() == null) {
			
			cupom.setStatus(StatusCupom.ATIVO);
			
		}
		
	}

}
