package com.bndesigner.dto.response.cupom;

import java.math.BigDecimal;

public record ValidacaoCupomResponse(

        Boolean valido,
        String codigo,
        BigDecimal descontoPercentual,
        String mensagem

) {}
