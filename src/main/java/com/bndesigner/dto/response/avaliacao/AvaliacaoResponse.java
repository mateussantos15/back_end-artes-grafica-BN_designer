package com.bndesigner.dto.response.avaliacao;

import java.time.LocalDateTime;

public record AvaliacaoResponse(

        Long idAvaliacao,

        Long produtoId,

        Long usuarioId,

        Integer nota,

        String comentario,

        LocalDateTime dataAvaliacao

) {
}