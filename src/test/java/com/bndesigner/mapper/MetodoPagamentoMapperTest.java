package com.bndesigner.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.bndesigner.domain.entity.metodopagamento.MetodoPagamento;
import com.bndesigner.dto.response.metodopagamento.MetodoPagamentoResponse;
import com.bndesigner.mapper.metodopagamento.MetodoPagamentoMapper;
import com.bndesigner.dto.request.metodopagamento.MetodoPagamentoCreateRequest;
import com.bndesigner.dto.request.metodopagamento.MetodoPagamentoUpdateRequest;

@DisplayName("MetodoPagamentoMapper")
class MetodoPagamentoMapperTest {

    private MetodoPagamentoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(MetodoPagamentoMapper.class);
    }

    @Test
    @DisplayName("deve converter CreateRequest para entidade")
    void deveConverterCreateRequestParaEntidade() {

        MetodoPagamentoCreateRequest request =
                new MetodoPagamentoCreateRequest(
                        "PIX",
                        "Pagamento via Pix",
                        true);

        MetodoPagamento resultado = mapper.toEntity(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNomeMetodoPagamento()).isEqualTo("PIX");
        assertThat(resultado.getDescricaoMetodoPagamento()).isEqualTo("Pagamento via Pix");
        assertThat(resultado.getAtivo()).isTrue();
        assertThat(resultado.getIdMetodoPagamento()).isNull();
    }

    @Test
    @DisplayName("deve converter entidade para Response")
    void deveConverterEntidadeParaResponse() {

        MetodoPagamento metodo = MetodoPagamento.builder()
                .idMetodoPagamento(1L)
                .nomeMetodoPagamento("PIX")
                .descricaoMetodoPagamento("Pagamento via Pix")
                .ativo(true)
                .build();

        MetodoPagamentoResponse resultado =
                mapper.toResponse(metodo);

        assertThat(resultado).isNotNull();
        assertThat(resultado.idMetodoPagamento()).isEqualTo(1L);
        assertThat(resultado.nomeMetodoPagamento()).isEqualTo("PIX");
        assertThat(resultado.descricaoMetodoPagamento()).isEqualTo("Pagamento via Pix");
        assertThat(resultado.ativo()).isTrue();
    }

    @Test
    @DisplayName("deve atualizar entidade com dados do UpdateRequest")
    void deveAtualizarEntidade() {

        MetodoPagamento metodo = MetodoPagamento.builder()
                .idMetodoPagamento(1L)
                .nomeMetodoPagamento("PIX")
                .descricaoMetodoPagamento("Descrição antiga")
                .ativo(true)
                .build();

        MetodoPagamentoUpdateRequest request =
                new MetodoPagamentoUpdateRequest(
                        "PIX",
                        "Descrição atualizada",
                        false);

        mapper.updateEntityFromRequest(request, metodo);

        assertThat(metodo.getIdMetodoPagamento()).isEqualTo(1L);
        assertThat(metodo.getNomeMetodoPagamento()).isEqualTo("PIX");
        assertThat(metodo.getDescricaoMetodoPagamento()).isEqualTo("Descrição atualizada");
        assertThat(metodo.getAtivo()).isFalse();
    }
}