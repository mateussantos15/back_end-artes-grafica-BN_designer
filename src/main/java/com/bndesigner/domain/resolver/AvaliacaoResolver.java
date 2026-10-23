package com.bndesigner.domain.resolver;

import org.springframework.stereotype.Component;

import com.bndesigner.domain.entity.avaliacao.Avaliacao;
import com.bndesigner.repository.avaliacao.AvaliacaoRepository;
import com.bndesigner.util.EntityLookup;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AvaliacaoResolver {

    private final AvaliacaoRepository avaliacaoRepository;

    public Avaliacao buscarAvaliacao(Long avaliacaoId) {
        return EntityLookup.buscarOuLancar(
                avaliacaoRepository,
                avaliacaoId,
                "Avaliação");
    }
}