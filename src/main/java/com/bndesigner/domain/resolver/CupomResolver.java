package com.bndesigner.domain.resolver;

import org.springframework.stereotype.Component;

import com.bndesigner.domain.entity.cupom.Cupom;
import com.bndesigner.domain.validation.CupomValidator;
import com.bndesigner.exceptions.custom.ResourceNotFoundException;
import com.bndesigner.repository.cupom.CupomRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CupomResolver {

    private final CupomRepository cupomRepository;
    private final CupomValidator cupomValidator;

    public Cupom buscarCupomValido(String codigoCupom) {

        // cupom opcional
        if (codigoCupom == null || codigoCupom.isBlank()) {
            return null;
        }

        Cupom cupom = cupomRepository
                .findByCodigoIgnoreCase(codigoCupom)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "Cupom",
                                codigoCupom
                        )
                );

        cupomValidator.validarCupomUtilizavel(cupom);

        return cupom;
    }
}