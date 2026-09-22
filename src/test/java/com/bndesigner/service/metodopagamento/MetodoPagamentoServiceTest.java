package com.bndesigner.service.metodopagamento;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bndesigner.domain.entity.metodopagamento.MetodoPagamento;
import com.bndesigner.dto.request.metodopagamento.MetodoPagamentoCreateRequest;
import com.bndesigner.dto.response.metodopagamento.MetodoPagamentoResponse;
import com.bndesigner.dto.request.metodopagamento.MetodoPagamentoUpdateRequest;
import com.bndesigner.exceptions.custom.ResourceNotFoundException;
import com.bndesigner.mapper.metodopagamento.MetodoPagamentoMapper;
import com.bndesigner.repository.metodopagamento.MetodoPagamentoRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("MetodoPagamentoService")
class MetodoPagamentoServiceTest {

    @Mock
    private MetodoPagamentoRepository metodoPagamentoRepository;

    @Mock
    private MetodoPagamentoMapper metodoPagamentoMapper;

    @InjectMocks
    private MetodoPagamentoServiceImpl metodoPagamentoService;

    private MetodoPagamento metodoAtivo;
    private MetodoPagamento metodoInativo;

    private MetodoPagamentoResponse responseAtivo;

    @BeforeEach
    void setUp() {

        metodoAtivo = MetodoPagamento.builder()
                .idMetodoPagamento(1L)
                .nomeMetodoPagamento("PIX")
                .descricaoMetodoPagamento("Pagamento via Pix")
                .ativo(true)
                .build();

        metodoInativo = MetodoPagamento.builder()
                .idMetodoPagamento(2L)
                .nomeMetodoPagamento("Boleto")
                .descricaoMetodoPagamento("Pagamento via boleto")
                .ativo(false)
                .build();

        responseAtivo = new MetodoPagamentoResponse(
                1L,
                "PIX",
                "Pagamento via Pix",
                true);
    }

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("deve criar método de pagamento")
        void deveCriarMetodoPagamento() {

            MetodoPagamentoCreateRequest request =
                    new MetodoPagamentoCreateRequest(
                            "PIX",
                            "Pagamento via Pix",
                            true);

            when(metodoPagamentoMapper.toEntity(request))
                    .thenReturn(metodoAtivo);

            when(metodoPagamentoRepository.save(metodoAtivo))
                    .thenReturn(metodoAtivo);

            when(metodoPagamentoMapper.toResponse(metodoAtivo))
                    .thenReturn(responseAtivo);

            MetodoPagamentoResponse resultado =
                    metodoPagamentoService.criar(request);

            assertThat(resultado).isEqualTo(responseAtivo);

            verify(metodoPagamentoMapper).toEntity(request);
            verify(metodoPagamentoRepository).save(metodoAtivo);
            verify(metodoPagamentoMapper).toResponse(metodoAtivo);
        }

        @Test
        @DisplayName("deve definir método como ativo quando ativo não informado")
        void deveDefinirComoAtivoQuandoAtivoNaoInformado() {

            MetodoPagamentoCreateRequest request =
                    new MetodoPagamentoCreateRequest(
                            "PIX",
                            "Pagamento via Pix",
                            null);

            MetodoPagamento metodo =
                    MetodoPagamento.builder()
                            .nomeMetodoPagamento("PIX")
                            .descricaoMetodoPagamento("Pagamento via Pix")
                            .ativo(null)
                            .build();

            when(metodoPagamentoMapper.toEntity(request))
                    .thenReturn(metodo);

            when(metodoPagamentoRepository.save(metodo))
                    .thenReturn(metodo);

            when(metodoPagamentoMapper.toResponse(metodo))
                    .thenReturn(responseAtivo);

            metodoPagamentoService.criar(request);

            assertThat(metodo.getAtivo()).isTrue();

            verify(metodoPagamentoRepository).save(metodo);
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve buscar método existente")
        void deveBuscarMetodoExistente() {

            when(metodoPagamentoRepository.findById(1L))
                    .thenReturn(Optional.of(metodoAtivo));

            when(metodoPagamentoMapper.toResponse(metodoAtivo))
                    .thenReturn(responseAtivo);

            MetodoPagamentoResponse resultado =
                    metodoPagamentoService.buscarPorId(1L);

            assertThat(resultado).isEqualTo(responseAtivo);

            verify(metodoPagamentoRepository).findById(1L);
            verify(metodoPagamentoMapper).toResponse(metodoAtivo);
        }

        @Test
        @DisplayName("deve permitir buscar método inativo")
        void devePermitirBuscarMetodoInativo() {

            MetodoPagamentoResponse response =
                    new MetodoPagamentoResponse(
                            2L,
                            "Boleto",
                            "Pagamento via boleto",
                            false);

            when(metodoPagamentoRepository.findById(2L))
                    .thenReturn(Optional.of(metodoInativo));

            when(metodoPagamentoMapper.toResponse(metodoInativo))
                    .thenReturn(response);

            MetodoPagamentoResponse resultado =
                    metodoPagamentoService.buscarPorId(2L);

            assertThat(resultado).isEqualTo(response);
            assertThat(resultado.ativo()).isFalse();

            verify(metodoPagamentoRepository).findById(2L);
        }

        @Test
        @DisplayName("deve lançar exceção quando método não existe")
        void deveLancarExcecaoQuandoNaoExiste() {

            when(metodoPagamentoRepository.findById(999L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(
                    () -> metodoPagamentoService.buscarPorId(999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(metodoPagamentoRepository).findById(999L);
            verifyNoInteractions(metodoPagamentoMapper);
        }
    }

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("deve listar métodos de pagamento")
        void deveListarMetodosPagamento() {

            MetodoPagamentoResponse responseBoleto =
                    new MetodoPagamentoResponse(
                            2L,
                            "Boleto",
                            "Pagamento via boleto",
                            false);

            when(metodoPagamentoRepository.findAll())
                    .thenReturn(List.of(metodoAtivo, metodoInativo));

            when(metodoPagamentoMapper.toResponse(metodoAtivo))
                    .thenReturn(responseAtivo);

            when(metodoPagamentoMapper.toResponse(metodoInativo))
                    .thenReturn(responseBoleto);

            List<MetodoPagamentoResponse> resultado =
                    metodoPagamentoService.listar();

            assertThat(resultado)
                    .hasSize(2)
                    .containsExactly(responseAtivo, responseBoleto);

            verify(metodoPagamentoRepository).findAll();
            verify(metodoPagamentoMapper).toResponse(metodoAtivo);
            verify(metodoPagamentoMapper).toResponse(metodoInativo);
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não existem métodos")
        void deveRetornarListaVaziaQuandoNaoExistemMetodos() {

            when(metodoPagamentoRepository.findAll())
                    .thenReturn(List.of());

            List<MetodoPagamentoResponse> resultado =
                    metodoPagamentoService.listar();

            assertThat(resultado).isEmpty();

            verify(metodoPagamentoRepository).findAll();
            verifyNoInteractions(metodoPagamentoMapper);
        }
    }

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("deve atualizar método existente")
        void deveAtualizarMetodoExistente() {

            MetodoPagamentoUpdateRequest request =
                    new MetodoPagamentoUpdateRequest(
                            "PIX",
                            "Nova descrição",
                            true);

            when(metodoPagamentoRepository.findById(1L))
                    .thenReturn(Optional.of(metodoAtivo));

            doNothing()
                    .when(metodoPagamentoMapper)
                    .updateEntityFromRequest(request, metodoAtivo);

            when(metodoPagamentoMapper.toResponse(metodoAtivo))
                    .thenReturn(responseAtivo);

            MetodoPagamentoResponse resultado =
                    metodoPagamentoService.atualizar(1L, request);

            assertThat(resultado).isEqualTo(responseAtivo);

            verify(metodoPagamentoRepository).findById(1L);
            verify(metodoPagamentoMapper)
                    .updateEntityFromRequest(request, metodoAtivo);
            verify(metodoPagamentoMapper)
                    .toResponse(metodoAtivo);

            verify(metodoPagamentoRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("deve permitir atualizar método inativo")
        void devePermitirAtualizarMetodoInativo() {

            MetodoPagamentoUpdateRequest request =
                    new MetodoPagamentoUpdateRequest(
                            "Boleto",
                            "Nova descrição",
                            false);

            MetodoPagamentoResponse response =
                    new MetodoPagamentoResponse(
                            2L,
                            "Boleto",
                            "Nova descrição",
                            false);

            when(metodoPagamentoRepository.findById(2L))
                    .thenReturn(Optional.of(metodoInativo));

            when(metodoPagamentoMapper.toResponse(metodoInativo))
                    .thenReturn(response);

            MetodoPagamentoResponse resultado =
                    metodoPagamentoService.atualizar(2L, request);

            assertThat(resultado).isEqualTo(response);

            verify(metodoPagamentoMapper)
                    .updateEntityFromRequest(request, metodoInativo);
        }

        @Test
        @DisplayName("deve lançar exceção ao atualizar método inexistente")
        void deveLancarExcecaoAoAtualizarMetodoInexistente() {

            MetodoPagamentoUpdateRequest request =
                    new MetodoPagamentoUpdateRequest(
                            "PIX",
                            "Pagamento via Pix",
                            true);

            when(metodoPagamentoRepository.findById(999L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(
                    () -> metodoPagamentoService.atualizar(999L, request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(metodoPagamentoRepository).findById(999L);

            verify(metodoPagamentoMapper, never())
                    .updateEntityFromRequest(request, metodoAtivo);

            verify(metodoPagamentoMapper, never())
                    .toResponse(any());
        }
    }

    @Nested
    @DisplayName("desativar()")
    class Desativar {

        @Test
        @DisplayName("deve desativar método existente")
        void deveDesativarMetodoExistente() {

            when(metodoPagamentoRepository.findById(1L))
                    .thenReturn(Optional.of(metodoAtivo));

            metodoPagamentoService.desativar(1L);

            assertThat(metodoAtivo.getAtivo()).isFalse();

            verify(metodoPagamentoRepository).findById(1L);
            verify(metodoPagamentoRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("deve lançar exceção ao desativar método inexistente")
        void deveLancarExcecaoAoDesativarMetodoInexistente() {

            when(metodoPagamentoRepository.findById(999L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(
                    () -> metodoPagamentoService.desativar(999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(metodoPagamentoRepository).findById(999L);
        }
    }
}