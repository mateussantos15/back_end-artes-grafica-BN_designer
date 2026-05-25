package com.bndesigner.domain.validation;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.bndesigner.exceptions.custom.DuplicateResourceException;
import com.bndesigner.exceptions.custom.InvalidCupomException;
import com.bndesigner.repository.cupom.CupomRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CupomValidator {

    private final CupomRepository cupomRepository;

    public void validarCodigoDuplicado(String codigo, Long id) {

        if (id != null) {
            boolean ehOMesmoCodigo = cupomRepository.findById(id)
                    .map(c -> c.getCodigo().equalsIgnoreCase(codigo))
                    .orElse(false);

            if (ehOMesmoCodigo) return;
        }

        if (cupomRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new DuplicateResourceException("Cupom", "Código", codigo);
        }
    }

    public void validarDataValidade(LocalDate dataValidade) {

        if (dataValidade.isBefore(LocalDate.now())) {
            throw new InvalidCupomException(dataValidade);
        }
    }
}