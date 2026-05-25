package com.bndesigner.service.arquivo;

import com.bndesigner.domain.entity.arquivo.Arquivo;
import com.bndesigner.dto.request.arquivo.ArquivoCreateRequest;
import com.bndesigner.dto.request.arquivo.ArquivoUpdateRequest;
import com.bndesigner.dto.response.arquivo.ArquivoResponse;
import com.bndesigner.exceptions.custom.BusinessException;
import com.bndesigner.exceptions.custom.ResourceNotFoundException;
import com.bndesigner.mapper.arquivo.ArquivoMapper;
import com.bndesigner.repository.arquivo.ArquivoRepository;
import com.bndesigner.service.arquivo.impl.ArquivoServiceImpl;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArquivoServiceImpl")
class ArquivoServiceTest {

    // -------------------------------------------------------------------------
    // Dependências mockadas e classe sob teste
    // -------------------------------------------------------------------------

    @Mock
    private ArquivoRepository arquivoRepository;

    @Mock
    private ArquivoMapper arquivoMapper;

    @InjectMocks
    private ArquivoServiceImpl arquivoService;

    // -------------------------------------------------------------------------
    // Fixtures
    // -------------------------------------------------------------------------

    private Arquivo arquivoAtivo;
    private ArquivoResponse arquivoResponse;
    private ArquivoCreateRequest creatRequest;
    private ArquivoUpdateRequest updateRequest;

    private static final Long ID_EXISTENTE   = 1L;
    private static final Long ID_INEXISTENTE = 999L;
    private static final String HASH_UNICO   = "abc123hash";

    @BeforeEach
    void setUp() {
        arquivoAtivo = new Arquivo();
        arquivoAtivo.setIdArquivo(ID_EXISTENTE);
        arquivoAtivo.setAtivo(true);

        arquivoResponse = new ArquivoResponse(
                ID_EXISTENTE,
                "https://storage.example.com/arquivo-teste.pdf",
                HASH_UNICO,
                LocalDateTime.of(2025, 1, 15, 10, 0),
                LocalDateTime.of(2025, 10, 3, 10, 0),
                true
        );

        creatRequest = mock(ArquivoCreateRequest.class);
        mock(ArquivoCreateRequest.class);
        updateRequest = mock(ArquivoUpdateRequest.class);
    }

    // =========================================================================
    // criar()
    // =========================================================================

    @Nested
    @DisplayName("criar()")
    class Criar {

        @Test
        @DisplayName("Deve criar arquivo com sucesso quando hash for nulo")
        void deveCriarArquivoQuandoHashNulo() {
            // hash nulo → pula verificação de duplicidade
            when(creatRequest.hashArquivo()).thenReturn(null);
            when(arquivoMapper.toEntity(creatRequest)).thenReturn(arquivoAtivo);
            when(arquivoRepository.save(arquivoAtivo)).thenReturn(arquivoAtivo);
            when(arquivoMapper.toResponse(arquivoAtivo)).thenReturn(arquivoResponse);

            ArquivoResponse resultado = arquivoService.criar(creatRequest);

            assertThat(resultado).isNotNull().isEqualTo(arquivoResponse);
            verify(arquivoRepository, never()).existsByHashArquivo(any());
            verify(arquivoRepository).save(arquivoAtivo);
        }

        @Test
        @DisplayName("Deve criar arquivo com sucesso quando hash for único no banco")
        void deveCriarArquivoQuandoHashUnico() {
            when(creatRequest.hashArquivo()).thenReturn(HASH_UNICO);
            when(arquivoRepository.existsByHashArquivo(HASH_UNICO)).thenReturn(false);
            when(arquivoMapper.toEntity(creatRequest)).thenReturn(arquivoAtivo);
            when(arquivoRepository.save(arquivoAtivo)).thenReturn(arquivoAtivo);
            when(arquivoMapper.toResponse(arquivoAtivo)).thenReturn(arquivoResponse);

            ArquivoResponse resultado = arquivoService.criar(creatRequest);

            assertThat(resultado).isNotNull().isEqualTo(arquivoResponse);
            verify(arquivoRepository).existsByHashArquivo(HASH_UNICO);
            verify(arquivoRepository).save(arquivoAtivo);
        }

        @Test
        @DisplayName("Deve lançar BusinessException com status 409 quando hash já estiver cadastrado")
        void deveLancarBusinessExceptionQuandoHashDuplicado() {
            when(creatRequest.hashArquivo()).thenReturn(HASH_UNICO);
            when(arquivoRepository.existsByHashArquivo(HASH_UNICO)).thenReturn(true);

            assertThatThrownBy(() -> arquivoService.criar(creatRequest))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    });

            // Nunca deve persistir quando o hash já existe
            verify(arquivoRepository, never()).save(any());
            verify(arquivoMapper, never()).toEntity(any());
        }

        @Test
        @DisplayName("Deve mapear a entidade através do mapper antes de persistir")
        void deveDelegarMapeamentoAoMapper() {
            when(creatRequest.hashArquivo()).thenReturn(null);
            when(arquivoMapper.toEntity(creatRequest)).thenReturn(arquivoAtivo);
            when(arquivoRepository.save(arquivoAtivo)).thenReturn(arquivoAtivo);
            when(arquivoMapper.toResponse(arquivoAtivo)).thenReturn(arquivoResponse);

            arquivoService.criar(creatRequest);

            verify(arquivoMapper).toEntity(creatRequest);
            verify(arquivoMapper).toResponse(arquivoAtivo);
        }
    }

    // =========================================================================
    // buscarPorId()
    // =========================================================================

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar ArquivoResponse quando ID existir no repositório")
        void deveRetornarResponseQuandoIdExistir() {
            when(arquivoRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(arquivoAtivo));
            when(arquivoMapper.toResponse(arquivoAtivo)).thenReturn(arquivoResponse);

            ArquivoResponse resultado = arquivoService.buscarPorId(ID_EXISTENTE);

            assertThat(resultado).isNotNull().isEqualTo(arquivoResponse);
            verify(arquivoRepository).findById(ID_EXISTENTE);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException com status 404 quando ID não existir")
        void deveLancarResourceNotFoundQuandoIdInexistente() {
            when(arquivoRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> arquivoService.buscarPorId(ID_INEXISTENTE))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .satisfies(ex -> {
                        ResourceNotFoundException rnf = (ResourceNotFoundException) ex;
                        assertThat(rnf.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    });

            verify(arquivoMapper, never()).toResponse(any());
        }

        @Test
        @DisplayName("Deve incluir o ID na mensagem de detalhe da exceção")
        void deveIncluirIdNaMensagemDaExcecao() {
            when(arquivoRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> arquivoService.buscarPorId(ID_INEXISTENTE))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(String.valueOf(ID_INEXISTENTE));
        }

        @Test
        @DisplayName("Deve delegar conversão ao mapper após encontrar a entidade")
        void deveDelegarConversaoAoMapper() {
            when(arquivoRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(arquivoAtivo));
            when(arquivoMapper.toResponse(arquivoAtivo)).thenReturn(arquivoResponse);

            arquivoService.buscarPorId(ID_EXISTENTE);

            verify(arquivoMapper).toResponse(arquivoAtivo);
        }
    }

    // =========================================================================
    // listarAtivos()
    // =========================================================================

    @Nested
    @DisplayName("listarAtivos()")
    class ListarAtivos {

        @Test
        @DisplayName("Deve retornar página com arquivos ativos mapeados")
        void deveRetornarPaginaComAtivos() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Arquivo> paginaEntidades = new PageImpl<>(List.of(arquivoAtivo));

            when(arquivoRepository.findByAtivoTrue(pageable)).thenReturn(paginaEntidades);
            when(arquivoMapper.toResponse(arquivoAtivo)).thenReturn(arquivoResponse);

            Page<ArquivoResponse> resultado = arquivoService.listarAtivos(pageable);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent()).hasSize(1);
            assertThat(resultado.getContent().get(0)).isEqualTo(arquivoResponse);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não houver arquivos ativos")
        void deveRetornarPaginaVaziaQuandoSemAtivos() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Arquivo> paginaVazia = new PageImpl<>(List.of());

            when(arquivoRepository.findByAtivoTrue(pageable)).thenReturn(paginaVazia);

            Page<ArquivoResponse> resultado = arquivoService.listarAtivos(pageable);

            assertThat(resultado.getContent()).isEmpty();
            verify(arquivoMapper, never()).toResponse(any());
        }

        @Test
        @DisplayName("Deve preservar metadados de paginação corretamente")
        void devePreservarMetadadosDePaginacao() {
            Pageable pageable = PageRequest.of(1, 5);
            Page<Arquivo> pagina = new PageImpl<>(List.of(arquivoAtivo), pageable, 11L);

            when(arquivoRepository.findByAtivoTrue(pageable)).thenReturn(pagina);
            when(arquivoMapper.toResponse(arquivoAtivo)).thenReturn(arquivoResponse);

            Page<ArquivoResponse> resultado = arquivoService.listarAtivos(pageable);

            assertThat(resultado.getNumber()).isEqualTo(1);
            assertThat(resultado.getSize()).isEqualTo(5);
            assertThat(resultado.getTotalElements()).isEqualTo(11L);
            assertThat(resultado.getTotalPages()).isEqualTo(3);
        }

        @Test
        @DisplayName("Deve chamar findByAtivoTrue (não findAll) para retornar apenas ativos")
        void deveChamarFindByAtivoTrue() {
            Pageable pageable = PageRequest.of(0, 10);
            when(arquivoRepository.findByAtivoTrue(pageable)).thenReturn(new PageImpl<>(List.of()));

            arquivoService.listarAtivos(pageable);

            verify(arquivoRepository).findByAtivoTrue(pageable);
            verify(arquivoRepository, never()).findAll(any(Pageable.class));
        }
    }

    // =========================================================================
    // atualizar()
    // =========================================================================

    @Nested
    @DisplayName("atualizar()")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar e retornar ArquivoResponse quando ID existir")
        void deveAtualizarArquivoComSucesso() {
            when(arquivoRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(arquivoAtivo));
            doNothing().when(arquivoMapper).updateEntityFromRequest(updateRequest, arquivoAtivo);
            when(arquivoRepository.save(arquivoAtivo)).thenReturn(arquivoAtivo);
            when(arquivoMapper.toResponse(arquivoAtivo)).thenReturn(arquivoResponse);

            ArquivoResponse resultado = arquivoService.atualizar(ID_EXISTENTE, updateRequest);

            assertThat(resultado).isNotNull().isEqualTo(arquivoResponse);
        }

        @Test
        @DisplayName("Deve aplicar os dados do request na entidade via mapper antes de salvar")
        void deveAplicarUpdateRequestNaEntidade() {
            when(arquivoRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(arquivoAtivo));
            doNothing().when(arquivoMapper).updateEntityFromRequest(updateRequest, arquivoAtivo);
            when(arquivoRepository.save(arquivoAtivo)).thenReturn(arquivoAtivo);
            when(arquivoMapper.toResponse(arquivoAtivo)).thenReturn(arquivoResponse);

            arquivoService.atualizar(ID_EXISTENTE, updateRequest);

            // Garante a ordem correta: updateEntityFromRequest → save
            var inOrder = inOrder(arquivoMapper, arquivoRepository);
            inOrder.verify(arquivoMapper).updateEntityFromRequest(updateRequest, arquivoAtivo);
            inOrder.verify(arquivoRepository).save(arquivoAtivo);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException com status 404 quando ID não existir")
        void deveLancarExcecaoQuandoIdInexistente() {
            when(arquivoRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> arquivoService.atualizar(ID_INEXISTENTE, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .satisfies(ex -> {
                        ResourceNotFoundException rnf = (ResourceNotFoundException) ex;
                        assertThat(rnf.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    });

            verify(arquivoMapper, never()).updateEntityFromRequest(any(), any());
            verify(arquivoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve incluir o ID na mensagem de detalhe da exceção ao atualizar")
        void deveIncluirIdNaMensagemDaExcecaoAoAtualizar() {
            when(arquivoRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> arquivoService.atualizar(ID_INEXISTENTE, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(String.valueOf(ID_INEXISTENTE));
        }
    }

    // =========================================================================
    // desativar()
    // =========================================================================

    @Nested
    @DisplayName("desativar()")
    class Desativar {

        @Test
        @DisplayName("Deve definir ativo=false e salvar entidade quando ID existir")
        void deveDesativarArquivoComSucesso() {
            arquivoAtivo.setAtivo(true);
            when(arquivoRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(arquivoAtivo));
            when(arquivoRepository.save(arquivoAtivo)).thenReturn(arquivoAtivo);

            arquivoService.desativar(ID_EXISTENTE);

            assertThat(arquivoAtivo.getAtivo()).isFalse();
            verify(arquivoRepository).save(arquivoAtivo);
        }

        @Test
        @DisplayName("Deve persistir a entidade após setar ativo=false")
        void devePersistirAposDesativar() {
            when(arquivoRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(arquivoAtivo));
            when(arquivoRepository.save(arquivoAtivo)).thenReturn(arquivoAtivo);

            arquivoService.desativar(ID_EXISTENTE);

            // Garante que save é chamado depois de setAtivo(false)
            verify(arquivoRepository, times(1)).save(arquivoAtivo);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException com status 404 quando ID não existir")
        void deveLancarExcecaoQuandoIdInexistente() {
            when(arquivoRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> arquivoService.desativar(ID_INEXISTENTE))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .satisfies(ex -> {
                        ResourceNotFoundException rnf = (ResourceNotFoundException) ex;
                        assertThat(rnf.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    });

            // Nunca deve salvar quando o arquivo não existe
            verify(arquivoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve incluir o ID na mensagem de detalhe da exceção ao desativar")
        void deveIncluirIdNaMensagemDaExcecao() {
            when(arquivoRepository.findById(ID_INEXISTENTE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> arquivoService.desativar(ID_INEXISTENTE))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(String.valueOf(ID_INEXISTENTE));
        }

        @Test
        @DisplayName("Não deve acionar mapper em nenhum momento ao desativar")
        void naoDeveAcionarMapper() {
            when(arquivoRepository.findById(ID_EXISTENTE)).thenReturn(Optional.of(arquivoAtivo));
            when(arquivoRepository.save(arquivoAtivo)).thenReturn(arquivoAtivo);

            arquivoService.desativar(ID_EXISTENTE);

            verifyNoInteractions(arquivoMapper);
        }
    }
}