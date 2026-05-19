package com.bndesigner.service.cupom.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bndesigner.domain.entity.cupom.Cupom;
import com.bndesigner.domain.enums.cupom.StatusCupom;
import com.bndesigner.dto.request.cupom.CupomCreateRequest;
import com.bndesigner.dto.request.cupom.CupomUpdateRequest;
import com.bndesigner.dto.response.cupom.CupomResponse;
import com.bndesigner.dto.response.cupom.ValidacaoCupomResponse;
import com.bndesigner.mapper.cupom.CupomMapper;
import com.bndesigner.repository.cupom.CupomRepository;
import com.bndesigner.service.cupom.CupomService;
import com.bndesigner.service.validation.CupomValidator;
import com.bndesigner.util.EntityLookup;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CupomServiceImpl implements CupomService {
	
	private final CupomRepository cupomRepository;
	private final CupomValidator cupomValidator;
	private final CupomMapper cupomMapper;

	@Override
	public CupomResponse criar(CupomCreateRequest createRequest) {
		
		cupomValidator.validarCodigoDuplicado(createRequest.codigo(), null);
		
		cupomValidator.validarDataValidade(createRequest.dataValidade());
		
		Cupom cupom = cupomMapper.toEntity(createRequest);
		
		cupom.atualizarStatus(cupom);
		
		Cupom salvo = cupomRepository.save(cupom);
		
		return cupomMapper.toResponse(salvo);
	}

	@Override
	@Transactional(readOnly = true)
	public CupomResponse buscarPorId(Long id) {
		
		Cupom cupom = EntityLookup.buscarOuLancar(cupomRepository, id, "Cupom");
		
		cupom.atualizarStatus(cupom);
		
		return cupomMapper.toResponse(cupom);
	}

	@Override
	@Transactional
	public Page<CupomResponse> listar(Pageable pageable) {
		
		Page<Cupom> cuponsPage = cupomRepository.findAll(pageable);
		
		return cuponsPage.map(cupom -> {
			cupom.atualizarStatus(cupom);
			return cupomMapper.toResponse(cupom);
		});
	}

	@Override
	public CupomResponse atualizar(Long id, CupomUpdateRequest updateRequest) {
		
		Cupom cupom = EntityLookup.buscarOuLancar(cupomRepository, id, "Cupom");
		
		
		
		cupomValidator.validarCodigoDuplicado(updateRequest.codigo(), id);
		
		cupomValidator.validarDataValidade(updateRequest.dataValidade());
		
		cupomMapper.updateEntityFromRequest(updateRequest, cupom);
		
		cupom.atualizarStatus(cupom);
		
		Cupom atualizado = cupomRepository.save(cupom);
		
		return cupomMapper.toResponse(atualizado);
	}

	@Override
	public void deletar(Long id) {
		
		Cupom cupom = EntityLookup.buscarOuLancar(cupomRepository, id, "Cupom");
		
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

	    cupom.atualizarStatus(cupom);

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
}
