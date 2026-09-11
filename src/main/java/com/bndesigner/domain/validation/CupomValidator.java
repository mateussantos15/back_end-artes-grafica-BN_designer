package com.bndesigner.domain.validation;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.bndesigner.domain.entity.cupom.Cupom;
import com.bndesigner.domain.enums.cupom.StatusCupom;
import com.bndesigner.exceptions.custom.CupomExpiradoException;
import com.bndesigner.exceptions.custom.CupomInativoException;
import com.bndesigner.exceptions.custom.DuplicateResourceException;
import com.bndesigner.exceptions.custom.InvalidCupomException;
import com.bndesigner.repository.cupom.CupomRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CupomValidator {

    private final CupomRepository cupomRepository;

    // ─────────────────────────────────────
    // DUPLICIDADE
    // ─────────────────────────────────────

    public void validarCodigoDuplicado(
            String codigo,
            Long id
    ) {

        if (id != null) {

            boolean ehOMesmoCodigo = cupomRepository
                    .findById(id)
                    .map(c -> c.getCodigo()
                            .equalsIgnoreCase(codigo))
                    .orElse(false);

            if (ehOMesmoCodigo) {
                return;
            }
        }

        if (cupomRepository
                .existsByCodigoIgnoreCase(codigo)) {

            throw new DuplicateResourceException(
                    "Cupom",
                    "Código",
                    codigo
            );
        }
    }

    // ─────────────────────────────────────
    // VALIDADE
    // ─────────────────────────────────────

    public void validarDataValidade(
            LocalDate dataValidade
    ) {

        if (dataValidade == null) {
            return;
        }

        if (dataValidade.isBefore(LocalDate.now())) {

            throw new InvalidCupomException(
                    dataValidade
            );
        }
    }

    // ─────────────────────────────────────
    // STATUS
    // ─────────────────────────────────────

    public void validarStatusAtivo(
            Cupom cupom
    ) {

        if (cupom.getStatus() != StatusCupom.ATIVO) {

            throw new CupomInativoException(
                    cupom.getCodigo()
            );
        }
    }

    // ─────────────────────────────────────
    // UTILIZAÇÃO NO CHECKOUT
    // ─────────────────────────────────────

    public void validarCupomUtilizavel(
            Cupom cupom
    ) {

        validarStatusAtivo(cupom);

        if (cupom.getDataValidade() != null
                && cupom.getDataValidade()
                .isBefore(LocalDate.now())) {

            throw new CupomExpiradoException(
                    cupom.getCodigo(),
                    cupom.getDataValidade()
            );
        }
    }
}