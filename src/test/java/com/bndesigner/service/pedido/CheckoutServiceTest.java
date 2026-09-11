package com.bndesigner.service.pedido;

import com.bndesigner.domain.entity.cupom.Cupom;
import com.bndesigner.domain.entity.itemPedido.ItemPedido;
import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.domain.entity.produto.Produto;
import com.bndesigner.domain.enums.cupom.StatusCupom;
import com.bndesigner.domain.enums.pedido.StatusPedido;
import com.bndesigner.domain.resolver.CupomResolver;
import com.bndesigner.domain.resolver.ProdutoResolver;
import com.bndesigner.domain.validation.PedidoValidator;
import com.bndesigner.dto.request.itemPedido.ItemPedidoRequest;
import com.bndesigner.dto.request.pedido.PedidoCreatRequest;
import com.bndesigner.dto.response.pedido.PedidoResponse;
import com.bndesigner.exceptions.custom.ItemComQuantidadeInvalidaException;
import com.bndesigner.exceptions.custom.PedidoSemItensException;
import com.bndesigner.exceptions.custom.ResourceNotFoundException;
import com.bndesigner.exceptions.custom.CupomExpiradoException;
import com.bndesigner.exceptions.custom.CupomInativoException;
import com.bndesigner.mapper.pedido.PedidoMapper;
import com.bndesigner.repository.pedido.PedidoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CheckoutService")
class CheckoutServiceTest {

    @Mock private PedidoRepository    pedidoRepository;
    @Mock private ProdutoResolver     produtoResolver;
    @Mock private CupomResolver       cupomResolver;
    @Mock private PedidoValidator     pedidoValidator;
    @Mock private PedidoMapper        pedidoMapper;

    @InjectMocks
    private CheckoutService checkoutService;

    // ─── fixtures ───────────────────────────────────────────────────────────

    private Produto buildProduto(Long id, String titulo, BigDecimal preco) {
        return Produto.builder()
                .idProduto(id)
                .titulo(titulo)
                .preco(preco)
                .build();
    }

    private Cupom buildCupom(BigDecimal desconto) {
        return Cupom.builder()
                .id(1L)
                .codigo("PROMO10")
                .descontoPercentual(desconto)
                .status(StatusCupom.ATIVO)
                .dataValidade(LocalDate.now().plusDays(30))
                .build();
    }

    private ItemPedidoRequest itemRequest(Long produtoId, Integer quantidade) {
        return new ItemPedidoRequest(produtoId, quantidade);
    }

    private PedidoCreatRequest requestSemCupom(List<ItemPedidoRequest> itens) {
        return new PedidoCreatRequest(null, null, "cliente@email.com", "12345678901", itens);
    }

    private PedidoCreatRequest requestComCupom(String codigo, List<ItemPedidoRequest> itens) {
        return new PedidoCreatRequest(null, codigo, "cliente@email.com", "12345678901", itens);
    }

    private PedidoResponse responseFake(Long id, BigDecimal valorTotal) {
        return new PedidoResponse(id, StatusPedido.PENDENTE, valorTotal,
                null, null, null, "cliente@email.com", "12345678901", List.of());
    }

    // ─── fluxo feliz ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Fluxo feliz")
    class FluxoFeliz {

        @Test
        @DisplayName("deve criar pedido sem cupom e retornar PedidoResponse")
        void deveCriarPedidoSemCupom() {
            Produto produto = buildProduto(1L, "Produto A", new BigDecimal("50.00"));
            PedidoCreatRequest request = requestSemCupom(List.of(itemRequest(1L, 2)));
            Pedido pedidoSalvo = Pedido.builder()
                    .statusPedido(StatusPedido.PENDENTE)
                    .valorTotal(new BigDecimal("100.00"))
                    .build();

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido(null)).thenReturn(null);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produto);
            when(pedidoRepository.save(any())).thenReturn(pedidoSalvo);
            when(pedidoMapper.toResponse(pedidoSalvo)).thenReturn(responseFake(1L, new BigDecimal("100.00")));

            PedidoResponse response = checkoutService.criarPedido(request);

            assertThat(response).isNotNull();
            assertThat(response.valorTotal()).isEqualByComparingTo("100.00");
        }

        @Test
        @DisplayName("deve criar pedido com cupom de desconto e aplicar desconto corretamente")
        void deveCriarPedidoComCupom() {
            Produto produto = buildProduto(1L, "Produto A", new BigDecimal("100.00"));
            Cupom cupom = buildCupom(new BigDecimal("10")); // 10%
            PedidoCreatRequest request = requestComCupom("PROMO10", List.of(itemRequest(1L, 1)));
            Pedido pedidoSalvo = Pedido.builder()
                    .statusPedido(StatusPedido.PENDENTE)
                    .valorTotal(new BigDecimal("90.00"))
                    .build();

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido("PROMO10")).thenReturn(cupom);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produto);
            when(pedidoRepository.save(any())).thenReturn(pedidoSalvo);
            when(pedidoMapper.toResponse(pedidoSalvo)).thenReturn(responseFake(1L, new BigDecimal("90.00")));

            PedidoResponse response = checkoutService.criarPedido(request);

            assertThat(response.valorTotal()).isEqualByComparingTo("90.00");
        }

        @Test
        @DisplayName("deve calcular subtotal corretamente com múltiplos itens")
        void deveCalcularSubtotalComMultiplosItens() {
            // 2 x R$30 + 3 x R$20 = R$120
            Produto produtoA = buildProduto(1L, "Produto A", new BigDecimal("30.00"));
            Produto produtoB = buildProduto(2L, "Produto B", new BigDecimal("20.00"));
            PedidoCreatRequest request = requestSemCupom(
                    List.of(itemRequest(1L, 2), itemRequest(2L, 3)));

            ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido(null)).thenReturn(null);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produtoA);
            when(produtoResolver.buscarProdutoDisponivel(2L)).thenReturn(produtoB);
            when(pedidoRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
            when(pedidoMapper.toResponse(any())).thenReturn(responseFake(1L, new BigDecimal("120.00")));

            checkoutService.criarPedido(request);

            assertThat(captor.getValue().getValorTotal()).isEqualByComparingTo("120.00");
        }

        @Test
        @DisplayName("deve montar pedido com statusPedido PENDENTE")
        void deveMontarPedidoComStatusPendente() {
            Produto produto = buildProduto(1L, "Produto A", new BigDecimal("50.00"));
            PedidoCreatRequest request = requestSemCupom(List.of(itemRequest(1L, 1)));

            ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido(null)).thenReturn(null);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produto);
            when(pedidoRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
            when(pedidoMapper.toResponse(any())).thenReturn(responseFake(1L, new BigDecimal("50.00")));

            checkoutService.criarPedido(request);

            assertThat(captor.getValue().getStatusPedido()).isEqualTo(StatusPedido.PENDENTE);
        }

        @Test
        @DisplayName("deve relacionar todos os itens ao pedido antes de salvar")
        void deveRelacionarItensPedidoAntesDeсалvar() {
            Produto produto = buildProduto(1L, "Produto A", new BigDecimal("25.00"));
            PedidoCreatRequest request = requestSemCupom(
                    List.of(itemRequest(1L, 1), itemRequest(1L, 2)));

            ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido(null)).thenReturn(null);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produto);
            when(pedidoRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
            when(pedidoMapper.toResponse(any())).thenReturn(responseFake(1L, new BigDecimal("75.00")));

            checkoutService.criarPedido(request);

            Pedido pedido = captor.getValue();
            assertThat(pedido.getItens()).hasSize(2);
            assertThat(pedido.getItens())
                    .allSatisfy(item -> assertThat(item.getPedido()).isSameAs(pedido));
        }

        @Test
        @DisplayName("deve usar o preco do Produto como valorUnitario do item")
        void deveUsarPrecoDoProdutoComoValorUnitario() {
            Produto produto = buildProduto(1L, "Produto A", new BigDecimal("79.90"));
            PedidoCreatRequest request = requestSemCupom(List.of(itemRequest(1L, 1)));

            ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido(null)).thenReturn(null);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produto);
            when(pedidoRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
            when(pedidoMapper.toResponse(any())).thenReturn(responseFake(1L, new BigDecimal("79.90")));

            checkoutService.criarPedido(request);

            ItemPedido item = captor.getValue().getItens().getFirst();
            assertThat(item.getValorUnitario()).isEqualByComparingTo("79.90");
        }

        @Test
        @DisplayName("deve preencher emailCliente e cpf no pedido")
        void devePreencherEmailECpf() {
            Produto produto = buildProduto(1L, "Produto A", new BigDecimal("10.00"));
            PedidoCreatRequest request = requestSemCupom(List.of(itemRequest(1L, 1)));

            ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido(null)).thenReturn(null);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produto);
            when(pedidoRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
            when(pedidoMapper.toResponse(any())).thenReturn(responseFake(1L, new BigDecimal("10.00")));

            checkoutService.criarPedido(request);

            assertThat(captor.getValue().getEmailCliente()).isEqualTo("cliente@email.com");
            assertThat(captor.getValue().getCpf()).isEqualTo("12345678901");
        }

        @Test
        @DisplayName("deve vincular usuário ao pedido quando usuarioId é fornecido")
        void deveVincularUsuarioAoPedido() {
            Produto produto = buildProduto(1L, "Produto A", new BigDecimal("10.00"));
            PedidoCreatRequest request = new PedidoCreatRequest(
                    42L, null, "cliente@email.com", null,
                    List.of(itemRequest(1L, 1)));

            ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido(null)).thenReturn(null);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produto);
            when(pedidoRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
            when(pedidoMapper.toResponse(any())).thenReturn(responseFake(1L, new BigDecimal("10.00")));

            checkoutService.criarPedido(request);

            assertThat(captor.getValue().getUsuario()).isNotNull();
            assertThat(captor.getValue().getUsuario().getIdUsuario()).isEqualTo(42L);
        }

        @Test
        @DisplayName("não deve vincular usuário ao pedido quando usuarioId é nulo")
        void naoDeveVincularUsuarioQuandoIdNulo() {
            Produto produto = buildProduto(1L, "Produto A", new BigDecimal("10.00"));
            PedidoCreatRequest request = requestSemCupom(List.of(itemRequest(1L, 1)));

            ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido(null)).thenReturn(null);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produto);
            when(pedidoRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
            when(pedidoMapper.toResponse(any())).thenReturn(responseFake(1L, new BigDecimal("10.00")));

            checkoutService.criarPedido(request);

            assertThat(captor.getValue().getUsuario()).isNull();
        }
    }

    // ─── cálculo de desconto ─────────────────────────────────────────────────

    @Nested
    @DisplayName("Cálculo de desconto")
    class CalculoDesconto {

        @Test
        @DisplayName("não deve aplicar desconto quando cupom é nulo")
        void naoDeveAplicarDescontoSemCupom() {
            Produto produto = buildProduto(1L, "Produto A", new BigDecimal("200.00"));
            PedidoCreatRequest request = requestSemCupom(List.of(itemRequest(1L, 1)));

            ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido(null)).thenReturn(null);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produto);
            when(pedidoRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
            when(pedidoMapper.toResponse(any())).thenReturn(responseFake(1L, new BigDecimal("200.00")));

            checkoutService.criarPedido(request);

            assertThat(captor.getValue().getValorTotal()).isEqualByComparingTo("200.00");
        }

        @Test
        @DisplayName("deve aplicar 10% de desconto corretamente")
        void deveAplicarDezPorCentoDesconto() {
            // 1 x R$100 - 10% = R$90
            Produto produto = buildProduto(1L, "Produto A", new BigDecimal("100.00"));
            Cupom cupom = buildCupom(new BigDecimal("10"));
            PedidoCreatRequest request = requestComCupom("PROMO10", List.of(itemRequest(1L, 1)));

            ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido("PROMO10")).thenReturn(cupom);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produto);
            when(pedidoRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
            when(pedidoMapper.toResponse(any())).thenReturn(responseFake(1L, new BigDecimal("90.00")));

            checkoutService.criarPedido(request);

            assertThat(captor.getValue().getValorTotal()).isEqualByComparingTo("90.00");
        }

        @Test
        @DisplayName("deve aplicar 50% de desconto corretamente")
        void deveAplicarCinquentaPorCentoDesconto() {
            // 2 x R$50 = R$100 - 50% = R$50
            Produto produto = buildProduto(1L, "Produto A", new BigDecimal("50.00"));
            Cupom cupom = buildCupom(new BigDecimal("50"));
            PedidoCreatRequest request = requestComCupom("PROMO50", List.of(itemRequest(1L, 2)));

            ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido("PROMO50")).thenReturn(cupom);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produto);
            when(pedidoRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
            when(pedidoMapper.toResponse(any())).thenReturn(responseFake(1L, new BigDecimal("50.00")));

            checkoutService.criarPedido(request);

            assertThat(captor.getValue().getValorTotal()).isEqualByComparingTo("50.00");
        }

        @Test
        @DisplayName("deve vincular cupom ao pedido quando fornecido")
        void deveVincularCupomAoPedido() {
            Produto produto = buildProduto(1L, "Produto A", new BigDecimal("100.00"));
            Cupom cupom = buildCupom(new BigDecimal("10"));
            PedidoCreatRequest request = requestComCupom("PROMO10", List.of(itemRequest(1L, 1)));

            ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido("PROMO10")).thenReturn(cupom);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produto);
            when(pedidoRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
            when(pedidoMapper.toResponse(any())).thenReturn(responseFake(1L, new BigDecimal("90.00")));

            checkoutService.criarPedido(request);

            assertThat(captor.getValue().getCupom()).isEqualTo(cupom);
        }
    }

    // ─── erros de validação do pedido ────────────────────────────────────────

    @Nested
    @DisplayName("Erros de validação — PedidoValidator")
    class ErrosValidacaoPedido {

        @Test
        @DisplayName("deve lançar PedidoSemItensException quando validator rejeita")
        void deveLancarPedidoSemItensException() {
            PedidoCreatRequest request = requestSemCupom(List.of());

            doThrow(new PedidoSemItensException())
                    .when(pedidoValidator).validarPedido(request);

            assertThatThrownBy(() -> checkoutService.criarPedido(request))
                    .isInstanceOf(PedidoSemItensException.class);

            verifyNoInteractions(cupomResolver, produtoResolver, pedidoRepository, pedidoMapper);
        }

        @Test
        @DisplayName("deve lançar ItemComQuantidadeInvalidaException quando validator rejeita")
        void deveLancarItemComQuantidadeInvalidaException() {
            PedidoCreatRequest request = requestSemCupom(List.of(itemRequest(1L, 0)));

            doThrow(new ItemComQuantidadeInvalidaException(1L))
                    .when(pedidoValidator).validarPedido(request);

            assertThatThrownBy(() -> checkoutService.criarPedido(request))
                    .isInstanceOf(ItemComQuantidadeInvalidaException.class);

            verifyNoInteractions(cupomResolver, produtoResolver, pedidoRepository, pedidoMapper);
        }
    }

    // ─── erros do CupomResolver ──────────────────────────────────────────────

    @Nested
    @DisplayName("Erros — CupomResolver")
    class ErrosCupomResolver {

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando cupom não existe")
        void deveLancarExcecaoQuandoCupomNaoExiste() {
            PedidoCreatRequest request = requestComCupom("INVALIDO", List.of(itemRequest(1L, 1)));

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido("INVALIDO"))
                    .thenThrow(new ResourceNotFoundException("Cupom", "INVALIDO"));

            assertThatThrownBy(() -> checkoutService.criarPedido(request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(produtoResolver, pedidoRepository, pedidoMapper);
        }

        @Test
        @DisplayName("deve lançar CupomInativoException quando cupom está inativo")
        void deveLancarExcecaoQuandoCupomInativo() {
            PedidoCreatRequest request = requestComCupom("INATIVO", List.of(itemRequest(1L, 1)));

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido("INATIVO"))
                    .thenThrow(new CupomInativoException("INATIVO"));

            assertThatThrownBy(() -> checkoutService.criarPedido(request))
                    .isInstanceOf(CupomInativoException.class);

            verifyNoInteractions(produtoResolver, pedidoRepository, pedidoMapper);
        }

        @Test
        @DisplayName("deve lançar CupomExpiradoException quando cupom está expirado")
        void deveLancarExcecaoQuandoCupomExpirado() {
            PedidoCreatRequest request = requestComCupom("EXPIRADO", List.of(itemRequest(1L, 1)));

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido("EXPIRADO"))
                    .thenThrow(new CupomExpiradoException("EXPIRADO", LocalDate.now().minusDays(1)));

            assertThatThrownBy(() -> checkoutService.criarPedido(request))
                    .isInstanceOf(CupomExpiradoException.class);

            verifyNoInteractions(produtoResolver, pedidoRepository, pedidoMapper);
        }
    }

    // ─── erros do ProdutoResolver ────────────────────────────────────────────

    @Nested
    @DisplayName("Erros — ProdutoResolver")
    class ErrosProdutoResolver {

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando produto não existe")
        void deveLancarExcecaoQuandoProdutoNaoExiste() {
            PedidoCreatRequest request = requestSemCupom(List.of(itemRequest(99L, 1)));

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido(null)).thenReturn(null);
            when(produtoResolver.buscarProdutoDisponivel(99L))
                    .thenThrow(new ResourceNotFoundException("Produto", "99"));

            assertThatThrownBy(() -> checkoutService.criarPedido(request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(pedidoRepository, pedidoMapper);
        }

        @Test
        @DisplayName("deve chamar buscarProdutoDisponivel para cada item do pedido")
        void deveChamarResolverParaCadaItem() {
            Produto produtoA = buildProduto(1L, "Produto A", new BigDecimal("10.00"));
            Produto produtoB = buildProduto(2L, "Produto B", new BigDecimal("20.00"));
            PedidoCreatRequest request = requestSemCupom(
                    List.of(itemRequest(1L, 1), itemRequest(2L, 2)));

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido(null)).thenReturn(null);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produtoA);
            when(produtoResolver.buscarProdutoDisponivel(2L)).thenReturn(produtoB);
            when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(pedidoMapper.toResponse(any())).thenReturn(responseFake(1L, new BigDecimal("50.00")));

            checkoutService.criarPedido(request);

            verify(produtoResolver).buscarProdutoDisponivel(1L);
            verify(produtoResolver).buscarProdutoDisponivel(2L);
        }
    }

    // ─── orquestração das dependências ───────────────────────────────────────

    @Nested
    @DisplayName("Orquestração das dependências")
    class Orquestracao {

        @Test
        @DisplayName("deve chamar todas as dependências na ordem correta")
        void deveChamarDependenciasNaOrdemCorreta() {
            Produto produto = buildProduto(1L, "Produto A", new BigDecimal("50.00"));
            PedidoCreatRequest request = requestSemCupom(List.of(itemRequest(1L, 1)));
            Pedido pedidoSalvo = Pedido.builder()
                    .statusPedido(StatusPedido.PENDENTE)
                    .valorTotal(new BigDecimal("50.00"))
                    .build();

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido(null)).thenReturn(null);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produto);
            when(pedidoRepository.save(any())).thenReturn(pedidoSalvo);
            when(pedidoMapper.toResponse(pedidoSalvo)).thenReturn(responseFake(1L, new BigDecimal("50.00")));

            var order = inOrder(pedidoValidator, cupomResolver, produtoResolver, pedidoRepository, pedidoMapper);

            checkoutService.criarPedido(request);

            order.verify(pedidoValidator).validarPedido(request);
            order.verify(cupomResolver).buscarCupomValido(null);
            order.verify(produtoResolver).buscarProdutoDisponivel(1L);
            order.verify(pedidoRepository).save(any());
            order.verify(pedidoMapper).toResponse(pedidoSalvo);
        }

        @Test
        @DisplayName("deve retornar exatamente o que o mapper retornar")
        void deveRetornarOQueOMapperRetornar() {
            Produto produto = buildProduto(1L, "Produto A", new BigDecimal("50.00"));
            PedidoCreatRequest request = requestSemCupom(List.of(itemRequest(1L, 1)));
            PedidoResponse esperado = responseFake(99L, new BigDecimal("50.00"));

            doNothing().when(pedidoValidator).validarPedido(request);
            when(cupomResolver.buscarCupomValido(null)).thenReturn(null);
            when(produtoResolver.buscarProdutoDisponivel(1L)).thenReturn(produto);
            when(pedidoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(pedidoMapper.toResponse(any())).thenReturn(esperado);

            PedidoResponse resultado = checkoutService.criarPedido(request);

            assertThat(resultado).isSameAs(esperado);
        }
    }
}