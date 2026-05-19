package com.bndesigner.service.cupom;

import com.bndesigner.domain.entity.cupom.Cupom;
import com.bndesigner.domain.enums.cupom.StatusCupom;
import com.bndesigner.dto.request.cupom.CupomCreateRequest;
import com.bndesigner.dto.request.cupom.CupomUpdateRequest;
import com.bndesigner.dto.response.cupom.CupomResponse;
import com.bndesigner.exceptions.BusinessException;
import com.bndesigner.exceptions.ResourceNotFoundException;
import com.bndesigner.mapper.cupom.CupomMapper;
import com.bndesigner.repository.cupom.CupomRepository;
import com.bndesigner.service.cupom.impl.CupomServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CupomServiceImpl")
class CupomServiceTest {

    @Mock
    private CupomRepository cupomRepository;

    @Mock
    private CupomMapper cupomMapper;

    @InjectMocks
    private CupomServiceImpl cupomService;

    // ---------- helpers de fixture ----------

    private Cupom cupomValido() {
        Cupom c = new Cupom();
        c.setId(1L);
        c.setCodigo("PROMO10");
        c.setDataValidade(LocalDate.now().plusDays(10));
        c.setStatus(StatusCupom.ATIVO);
        return c;
    }

    private CupomResponse responseValido(Cupom cupom) {
       
        return new CupomResponse(
        		cupom.getId(),
        		cupom.getCodigo(),
        		cupom.getDescontoPercentual(),
        		cupom.getDataValidade(),
        		cupom.getStatus());
    }

    // =========================================================
    //  criar
    // =========================================================
    @Nested
    @DisplayName("criar()")
    class Criar {

        private CupomCreateRequest requestValido;

        @BeforeEach
        void setUp() {
            requestValido = new CupomCreateRequest(
            		"PROMO10", 
            		new BigDecimal("0.10"),
            		LocalDate.now().plusDays(10));
        }

        @Test
        @DisplayName("deve criar cupom com sucesso quando dados são válidos")
        void deveCriarCupomComSucesso() {
            // Arrange
            Cupom entity = cupomValido();
            entity.setStatus(null); // antes de atualizarStatus
            CupomResponse response = responseValido(entity);

            when(cupomRepository.existsByCodigoIgnoreCase("PROMO10")).thenReturn(false);
            when(cupomMapper.toEntity(requestValido)).thenReturn(entity);
            when(cupomRepository.save(entity)).thenReturn(entity);
            when(cupomMapper.toResponse(entity)).thenReturn(response);

            // Act
            CupomResponse resultado = cupomService.criar(requestValido);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.codigo()).isEqualTo("PROMO10");
            assertThat(entity.getStatus()).isEqualTo(StatusCupom.ATIVO);
            verify(cupomRepository).save(entity);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando código já existe")
        void deveLancarExcecaoQuandoCodigoDuplicado() {
            // Arrange
            when(cupomRepository.existsByCodigoIgnoreCase("PROMO10")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> cupomService.criar(requestValido))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    });

            verify(cupomRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando data de validade já expirou")
        void deveLancarExcecaoQuandoDataExpirada() {
            // Arrange
            CupomCreateRequest requestExpirado =
                    new CupomCreateRequest(
                    		"PROMO10", 
                    		new BigDecimal("0.10"),
                    		LocalDate.now().minusDays(1));
            when(cupomRepository.existsByCodigoIgnoreCase("PROMO10")).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> cupomService.criar(requestExpirado))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });

            verify(cupomRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve marcar status como EXPIRADO quando data de validade é passada após passar validação")
        void deveMarcaStatusExpiradoQuandoDataPassada() {
            // Esse cenário testa atualizarStatusAutomaticamente diretamente via criar:
            // se por algum motivo a entidade chega com dataValidade no passado o status é forçado.
            Cupom entity = cupomValido();
            entity.setDataValidade(LocalDate.now().minusDays(1));
            entity.setStatus(StatusCupom.ATIVO);

            // A validação de data lançaria exceção antes disso, mas testamos o método
            // privado indiretamente simulando que a validação foi pulada (ex: teste isolado).
            // Aqui cobrimos via buscarPorId onde não há validação de data no fluxo.
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(cupomMapper.toResponse(entity)).thenReturn(responseValido(entity));

            cupomService.buscarPorId(1L);

            assertThat(entity.getStatus()).isEqualTo(StatusCupom.EXPIRADO);
        }
    }

    // =========================================================
    //  buscarPorId
    // =========================================================
    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar CupomResponse quando cupom existe")
        void deveRetornarCupomQuandoExiste() {
            // Arrange
            Cupom cupom = cupomValido();
            CupomResponse response = responseValido(cupom);

            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupom));
            when(cupomMapper.toResponse(cupom)).thenReturn(response);

            // Act
            CupomResponse resultado = cupomService.buscarPorId(1L);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando cupom não existe")
        void deveLancarExcecaoQuandoCupomNaoExiste() {
            // Arrange
            when(cupomRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> cupomService.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("deve atualizar status para EXPIRADO quando data de validade passou")
        void deveAtualizarStatusParaExpiradoAutomaticamente() {
            // Arrange
            Cupom cupom = cupomValido();
            cupom.setDataValidade(LocalDate.now().minusDays(1));
            cupom.setStatus(StatusCupom.ATIVO);

            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupom));
            when(cupomMapper.toResponse(cupom)).thenReturn(responseValido(cupom));

            // Act
            cupomService.buscarPorId(1L);

            // Assert
            assertThat(cupom.getStatus()).isEqualTo(StatusCupom.EXPIRADO);
        }

        @Test
        @DisplayName("deve manter status ATIVO quando data de validade é futura")
        void deveMaterStatusAtivoQuandoDataFutura() {
            // Arrange
            Cupom cupom = cupomValido(); // status=ATIVO, data futura
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupom));
            when(cupomMapper.toResponse(cupom)).thenReturn(responseValido(cupom));

            // Act
            cupomService.buscarPorId(1L);

            // Assert
            assertThat(cupom.getStatus()).isEqualTo(StatusCupom.ATIVO);
        }
    }

    // =========================================================
    //  listar
    // =========================================================
    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("deve retornar página de CupomResponse mapeada corretamente")
        void deveRetornarPaginaDeCupons() {
            // Arrange
            Cupom c1 = cupomValido();
            Cupom c2 = cupomValido();
            c2.setId(2L);
            c2.setCodigo("DESC20");

            Pageable pageable = PageRequest.of(0, 10);
            Page<Cupom> pagina = new PageImpl<>(List.of(c1, c2), pageable, 2);

            when(cupomRepository.findAll(pageable)).thenReturn(pagina);
            when(cupomMapper.toResponse(c1)).thenReturn(responseValido(c1));
            when(cupomMapper.toResponse(c2)).thenReturn(responseValido(c2));

            // Act
            Page<CupomResponse> resultado = cupomService.listar(pageable);

            // Assert
            assertThat(resultado.getTotalElements()).isEqualTo(2);
            assertThat(resultado.getContent()).hasSize(2);
            verify(cupomMapper, times(2)).toResponse(any(Cupom.class));
        }

        @Test
        @DisplayName("deve retornar página vazia quando não há cupons")
        void deveRetornarPaginaVaziaQuandoSemCupons() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(cupomRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

            // Act
            Page<CupomResponse> resultado = cupomService.listar(pageable);

            // Assert
            assertThat(resultado.getTotalElements()).isZero();
            verifyNoInteractions(cupomMapper);
        }

        @Test
        @DisplayName("deve atualizar status de cada cupom durante listagem")
        void deveAtualizarStatusDeCadaCupomNaListagem() {
            // Arrange
            Cupom expirado = cupomValido();
            expirado.setDataValidade(LocalDate.now().minusDays(1));
            expirado.setStatus(StatusCupom.ATIVO);

            Pageable pageable = PageRequest.of(0, 10);
            Page<Cupom> pagina = new PageImpl<>(List.of(expirado), pageable, 1);

            when(cupomRepository.findAll(pageable)).thenReturn(pagina);
            when(cupomMapper.toResponse(expirado)).thenReturn(responseValido(expirado));

            // Act
            cupomService.listar(pageable);

            // Assert
            assertThat(expirado.getStatus()).isEqualTo(StatusCupom.EXPIRADO);
        }
    }

    // =========================================================
    //  atualizar
    // =========================================================
    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        private CupomUpdateRequest requestValido;

        @BeforeEach
        void setUp() {
            requestValido = new CupomUpdateRequest(
            		"PROMO10", 
            		new BigDecimal("0.05"),
            		LocalDate.now().plusDays(5),
            		StatusCupom.EXPIRADO);
        }

        @Test
        @DisplayName("deve atualizar cupom com sucesso quando dados são válidos")
        void deveAtualizarCupomComSucesso() {
            // Arrange
            Cupom cupom = cupomValido(); // código = "PROMO10"
            CupomResponse response = responseValido(cupom);

            // findById é chamado 2x:
            //   1ª chamada → buscarCupomPorId (retorna o cupom)
            //   2ª chamada → validarCodigoDuplicado (verifica se o código pertence ao mesmo cupom)
            when(cupomRepository.findById(1L))
                    .thenReturn(Optional.of(cupom))  // 1ª chamada
                    .thenReturn(Optional.of(cupom)); // 2ª chamada
            when(cupomRepository.save(cupom)).thenReturn(cupom);
            when(cupomMapper.toResponse(cupom)).thenReturn(response);

            // Act
            CupomResponse resultado = cupomService.atualizar(1L, requestValido);

            // Assert
            assertThat(resultado).isNotNull();
            verify(cupomMapper).updateEntityFromRequest(requestValido, cupom);
            verify(cupomRepository).save(cupom);
            // Como ehOMesmoCodigo=true, existsByCodigoIgnoreCase nunca deve ser chamado
            verify(cupomRepository, never()).existsByCodigoIgnoreCase(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando cupom não existe")
        void deveLancarExcecaoQuandoCupomNaoExiste() {
            // Arrange
            when(cupomRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> cupomService.atualizar(99L, requestValido))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(cupomRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando novo código já pertence a outro cupom")
        void deveLancarExcecaoQuandoCodigoDuplicadoEmOutroCupom() {
            // Arrange
            Cupom cupom = cupomValido();
            cupom.setCodigo("OUTRO_CODIGO"); // código diferente do request

            CupomUpdateRequest requestComCodigoNovo = new CupomUpdateRequest(
            		"NOVOCOD", 
            		new BigDecimal("0.05"),
            		LocalDate.now().plusDays(5),
            		StatusCupom.INATIVO);

            // findById é chamado 2x:
            //   1ª chamada → buscarCupomPorId
            //   2ª chamada → validarCodigoDuplicado (ehOMesmoCodigo = false, pois o código mudou)
            when(cupomRepository.findById(1L))
                    .thenReturn(Optional.of(cupom))  // 1ª chamada
                    .thenReturn(Optional.of(cupom)); // 2ª chamada
            when(cupomRepository.existsByCodigoIgnoreCase("NOVOCOD")).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> cupomService.atualizar(1L, requestComCodigoNovo))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    });

            verify(cupomRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando data de validade é passada")
        void deveLancarExcecaoQuandoDataExpirada() {
            // Arrange
            Cupom cupom = cupomValido(); // código = "PROMO10"
            CupomUpdateRequest requestExpirado =
                    new CupomUpdateRequest(
                    		"PROMO10", 
                    		new BigDecimal("0.05"),
                    		LocalDate.now().minusDays(1),
                    		StatusCupom.ATIVO);

            // findById é chamado 2x:
            //   1ª chamada → buscarCupomPorId
            //   2ª chamada → validarCodigoDuplicado (ehOMesmoCodigo=true → retorna cedo, sem existsBy)
            when(cupomRepository.findById(1L))
                    .thenReturn(Optional.of(cupom))  // 1ª chamada
                    .thenReturn(Optional.of(cupom)); // 2ª chamada

            // Act & Assert
            assertThatThrownBy(() -> cupomService.atualizar(1L, requestExpirado))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    });

            verify(cupomRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve ignorar verificação de duplicidade quando código não foi alterado")
        void deveIgnorarVerificacaoDuplicidadeQuandoCodigoIgual() {
            // Arrange
            Cupom cupom = cupomValido(); // código = "PROMO10"
            CupomUpdateRequest mesmoCodigoRequest =
                    new CupomUpdateRequest(
                    		"PROMO10", 
                    		new BigDecimal("0.05"), 
                    		LocalDate.now().plusDays(5), 
                    		StatusCupom.ATIVO);

            // findById é chamado 2x:
            //   1ª chamada → buscarCupomPorId
            //   2ª chamada → validarCodigoDuplicado (ehOMesmoCodigo=true → sai sem chamar existsBy)
            when(cupomRepository.findById(1L))
                    .thenReturn(Optional.of(cupom))  // 1ª chamada
                    .thenReturn(Optional.of(cupom)); // 2ª chamada
            when(cupomRepository.save(cupom)).thenReturn(cupom);
            when(cupomMapper.toResponse(cupom)).thenReturn(responseValido(cupom));

            // Act
            cupomService.atualizar(1L, mesmoCodigoRequest);

            // Assert
            verify(cupomRepository, never()).existsByCodigoIgnoreCase(any());
        }
    }

    // =========================================================
    //  deletar
    // =========================================================
    @Nested
    @DisplayName("deletar()")
    class Deletar {

        @Test
        @DisplayName("deve deletar cupom quando ele existe")
        void deveDeletarCupomComSucesso() {
            // Arrange
            Cupom cupom = cupomValido();
            when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupom));

            // Act
            cupomService.deletar(1L);

            // Assert
            verify(cupomRepository).delete(cupom);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando cupom não existe")
        void deveLancarExcecaoQuandoCupomNaoExiste() {
            // Arrange
            when(cupomRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> cupomService.deletar(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(cupomRepository, never()).delete(any());
        }
    }
}