package com.bndesigner.service.validation;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.bndesigner.exceptions.BusinessException;
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
            throw new BusinessException(HttpStatus.CONFLICT,
                    "Código de cupom já cadastrado",
                    String.format("Já existe um cupom com esse código: '%s'", codigo));
        }
    }

    public void validarDataValidade(LocalDate dataValidade) {

        if (dataValidade.isBefore(LocalDate.now())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "A validade do código expirou",
                    String.format("A validade do cupom expirou em: '%s'.", dataValidade));
        }
    }
}