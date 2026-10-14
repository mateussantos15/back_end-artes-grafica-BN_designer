package com.bndesigner.dto.request.avaliacao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoUpdateRequest(

        @NotNull
        @Min(1)
        @Max(5)
        Integer nota,

        @Size(max = 1000)
        String comentario

		) {
}