package com.bndesigner.repository.itemPedido;

import com.bndesigner.config.JpaAuditingConfig;
import com.bndesigner.domain.entity.itempedido.ItemPedido;
import com.bndesigner.domain.entity.pedido.Pedido;
import com.bndesigner.domain.entity.produto.Produto;
import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.domain.enums.pedido.StatusPedido;
import com.bndesigner.domain.enums.usuario.TipoUsuario;
import com.bndesigner.repository.itempedido.ItemPedidoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("ItemPedidoRepository")
@Import(JpaAuditingConfig.class)
class ItemPedidoRepositoryTest {

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private TestEntityManager em;

    // ─── helpers ────────────────────────────────────────────────────────────

    private Usuario usuarioSalvo;
    private Usuario outroUsuarioSalvo;
    private Pedido pedidoSalvo;
    private Pedido outroPedidoSalvo;
    private Produto produtoSalvo;
    private Produto outroProdutoSalvo;
    private final Pageable pageable = PageRequest.of(0, 10);
    
	private Usuario buildUsuario(String nome, String email, String senha, TipoUsuario tipo) {
		
		return Usuario.builder()
				.nome(nome)
				.email(email)
				.senha(senha)
				.tipoUsuario(tipo)
				.build();
	}

    private Pedido buildPedido(StatusPedido status, BigDecimal valorTotal, Usuario usuario) {
        return Pedido.builder()
                .statusPedido(status)
                .valorTotal(valorTotal)
                .usuario(usuario)
                .build();
    }
 
    private Produto buildProduto(String titulo, BigDecimal preco) {
        return Produto.builder()
                .titulo(titulo)
                .preco(preco)
                .build();
    }
 
    private ItemPedido buildItem(Pedido pedido, Produto produto,
                                 Integer quantidade, BigDecimal valorUnitario) {
        return ItemPedido.builder()
                .pedido(pedido)
                .produto(produto)
                .quantidade(quantidade)
                .valorUnitario(valorUnitario)
                .build();
    }
 
    @BeforeEach
    void setUp() {
    	
    	usuarioSalvo = em.persistAndFlush(
    			buildUsuario(
    					new String("Mateus"),
    					new String("mateus@email.com"),
    					new String("hash_senha"),
    					TipoUsuario.CLIENTE
    			)
    	);
    	
    	outroUsuarioSalvo = em.persistAndFlush(
    			buildUsuario(
    					new String("João"),
    					new String("joao@email.com"),
    					new String("hash_senha"),
    					TipoUsuario.CLIENTE
    			)
    	);
    				
        pedidoSalvo = em.persistAndFlush(
        		buildPedido(
        				StatusPedido.PENDENTE, 
        				new BigDecimal("100.00"),
        				usuarioSalvo
        		)
        );
        
        outroPedidoSalvo = em.persistAndFlush(
        		buildPedido(
        				StatusPedido.PAGO,
        				new BigDecimal("200.00"),
        				outroUsuarioSalvo
        		)
        );
        
        produtoSalvo = em.persistAndFlush(
        		buildProduto(
        				"Produto A", 
        				new BigDecimal("49.90")
        		)
        );
        
        outroProdutoSalvo = em.persistAndFlush(
        		buildProduto(
        				"Produto B", 
        				new BigDecimal("29.90")
        		)
        );
    }


    // ─── CRUD básico ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Operações básicas (CRUD)")
    class CrudBasico {

        @Test
        @DisplayName("deve salvar e recuperar um ItemPedido pelo id")
        void deveSalvarERecuperarPorId() {
            ItemPedido item = buildItem(pedidoSalvo, produtoSalvo, 2, new BigDecimal("49.90"));

            ItemPedido salvo = itemPedidoRepository.save(item);
            em.flush();
            em.clear();

            Optional<ItemPedido> encontrado = itemPedidoRepository.findById(salvo.getIdItemPedido());

            assertThat(encontrado).isPresent();
            assertThat(encontrado.get().getQuantidade()).isEqualTo(2);
            assertThat(encontrado.get().getValorUnitario()).isEqualByComparingTo("49.90");
        }

        @Test
        @DisplayName("deve retornar Optional vazio para id inexistente")
        void deveRetornarVazioParaIdInexistente() {
            Optional<ItemPedido> resultado = itemPedidoRepository.findById(999L);

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("deve listar todos os itens salvos")
        void deveListarTodos() {
            itemPedidoRepository.save(buildItem(pedidoSalvo, produtoSalvo, 1, new BigDecimal("10.00")));
            itemPedidoRepository.save(buildItem(pedidoSalvo, outroProdutoSalvo, 3, new BigDecimal("20.00")));
            em.flush();
            em.clear();

            List<ItemPedido> todos = itemPedidoRepository.findAll();

            assertThat(todos).hasSize(2);
        }

        @Test
        @DisplayName("deve deletar um ItemPedido pelo id")
        void deveDeletarPorId() {
            ItemPedido salvo = itemPedidoRepository.save(
                    buildItem(pedidoSalvo, produtoSalvo, 1, new BigDecimal("15.00")));
            em.flush();

            itemPedidoRepository.deleteById(salvo.getIdItemPedido());
            em.flush();
            em.clear();

            assertThat(itemPedidoRepository.findById(salvo.getIdItemPedido())).isEmpty();
        }

        @Test
        @DisplayName("deve atualizar quantidade e valorUnitario de um item existente")
        void deveAtualizarCampos() {
            ItemPedido salvo = itemPedidoRepository.save(
                    buildItem(pedidoSalvo, produtoSalvo, 1, new BigDecimal("10.00")));
            em.flush();
            em.clear();

            ItemPedido encontrado = itemPedidoRepository.findById(salvo.getIdItemPedido()).orElseThrow();
            encontrado.setQuantidade(5);
            encontrado.setValorUnitario(new BigDecimal("99.99"));
            itemPedidoRepository.save(encontrado);
            em.flush();
            em.clear();

            ItemPedido atualizado = itemPedidoRepository.findById(salvo.getIdItemPedido()).orElseThrow();
            assertThat(atualizado.getQuantidade()).isEqualTo(5);
            assertThat(atualizado.getValorUnitario()).isEqualByComparingTo("99.99");
        }
    }

    // ─── findByPedidoId ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("findByPedidoId")
    class FindByPedidoId {

        @Test
        @DisplayName("deve retornar apenas os itens do pedido informado")
        void deveRetornarItensDoPedido() {
            itemPedidoRepository.save(buildItem(pedidoSalvo, produtoSalvo, 2, new BigDecimal("30.00")));
            itemPedidoRepository.save(buildItem(pedidoSalvo, outroProdutoSalvo, 1, new BigDecimal("50.00")));
            itemPedidoRepository.save(buildItem(outroPedidoSalvo, produtoSalvo, 3, new BigDecimal("10.00")));
            em.flush();
            em.clear();
           
            Page<ItemPedido> resultado = itemPedidoRepository.findByPedidoIdPedido(
            		pedidoSalvo.getIdPedido(), pageable);

            assertThat(resultado).hasSize(2);
            assertThat(resultado)
                    .allSatisfy(i -> assertThat(i.getPedido().getIdPedido()).isEqualTo(pedidoSalvo.getIdPedido()));
        }

        @Test
        @DisplayName("deve retornar lista vazia quando o pedido não tem itens")
        void deveRetornarListaVaziaParaPedidoSemItens() {
            Page<ItemPedido> resultado = itemPedidoRepository.findByPedidoIdPedido(
            		pedidoSalvo.getIdPedido(), pageable);

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("deve retornar lista vazia para id de pedido inexistente")
        void deveRetornarVazioParaPedidoInexistente() {
            Page<ItemPedido> resultado = itemPedidoRepository.findByPedidoIdPedido(
            		999L, pageable);

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("deve retornar somente um item quando há exatamente um vínculo")
        void deveRetornarUmItemExato() {
            itemPedidoRepository.save(buildItem(pedidoSalvo, produtoSalvo, 1, new BigDecimal("5.00")));
            em.flush();
            em.clear();

            Page<ItemPedido> resultado = itemPedidoRepository.findByPedidoIdPedido(
            		pedidoSalvo.getIdPedido(), pageable);

            assertThat(resultado).hasSize(1);
        }
    }

    // ─── findByProdutoId ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("findByProdutoId")
    class FindByProdutoId {

        @Test
        @DisplayName("deve retornar apenas os itens do produto informado")
        void deveRetornarItensDoProduto() {
            itemPedidoRepository.save(buildItem(pedidoSalvo, produtoSalvo, 2, new BigDecimal("25.00")));
            itemPedidoRepository.save(buildItem(outroPedidoSalvo, produtoSalvo, 1, new BigDecimal("25.00")));
            itemPedidoRepository.save(buildItem(pedidoSalvo, outroProdutoSalvo, 4, new BigDecimal("15.00")));
            em.flush();
            em.clear();

            Page<ItemPedido> resultado = itemPedidoRepository.findByProdutoIdProduto(
            		produtoSalvo.getIdProduto(), pageable);

            assertThat(resultado).hasSize(2);
            assertThat(resultado)
                    .allSatisfy(i -> assertThat(i.getProduto().getIdProduto()).
                    		isEqualTo(produtoSalvo.getIdProduto()));
        }

        @Test
        @DisplayName("deve retornar lista vazia quando nenhum item usa o produto")
        void deveRetornarListaVaziaParaProdutoSemItens() {
            Page<ItemPedido> resultado = itemPedidoRepository.findByProdutoIdProduto(
            		produtoSalvo.getIdProduto(), pageable);

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("deve retornar lista vazia para id de produto inexistente")
        void deveRetornarVazioParaProdutoInexistente() {
            Page<ItemPedido> resultado = itemPedidoRepository.findByProdutoIdProduto(
            		999L, pageable);

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("deve retornar somente um item quando há exatamente um vínculo")
        void deveRetornarUmItemExato() {
            itemPedidoRepository.save(buildItem(pedidoSalvo, produtoSalvo, 1, new BigDecimal("7.50")));
            em.flush();
            em.clear();

            Page<ItemPedido> resultado = itemPedidoRepository.findByProdutoIdProduto(
            		produtoSalvo.getIdProduto(), pageable);

            assertThat(resultado).hasSize(1);
        }
    }

    // ─── integridade dos relacionamentos ────────────────────────────────────

    @Nested
    @DisplayName("Integridade dos relacionamentos")
    class IntegridadeRelacionamentos {

        @Test
        @DisplayName("deve persistir relacionamento com Pedido corretamente")
        void devePersistirRelacionamentoComPedido() {
            ItemPedido salvo = itemPedidoRepository.save(
                    buildItem(pedidoSalvo, produtoSalvo, 1, BigDecimal.ONE));
            em.flush();
            em.clear();

            ItemPedido encontrado = itemPedidoRepository.findById(salvo.getIdItemPedido()).orElseThrow();

            assertThat(encontrado.getPedido()).isNotNull();
            assertThat(encontrado.getPedido().getIdPedido()).isEqualTo(pedidoSalvo.getIdPedido());
        }

        @Test
        @DisplayName("deve persistir relacionamento com Produto corretamente")
        void devePersistirRelacionamentoComProduto() {
            ItemPedido salvo = itemPedidoRepository.save(
                    buildItem(pedidoSalvo, produtoSalvo, 1, BigDecimal.ONE));
            em.flush();
            em.clear();

            ItemPedido encontrado = itemPedidoRepository.findById(salvo.getIdItemPedido()).orElseThrow();

            assertThat(encontrado.getProduto()).isNotNull();
            assertThat(encontrado.getProduto().getIdProduto()).isEqualTo(produtoSalvo.getIdProduto());
        }

        @Test
        @DisplayName("deve distinguir itens de pedidos diferentes com mesmo produto")
        void deveDisinguirItensDePedidosDiferentesComMesmoProduto() {
            itemPedidoRepository.save(buildItem(pedidoSalvo, produtoSalvo, 1, new BigDecimal("10.00")));
            itemPedidoRepository.save(buildItem(outroPedidoSalvo, produtoSalvo, 2, new BigDecimal("10.00")));
            em.flush();
            em.clear();

            Page<ItemPedido> doPrimeiro = itemPedidoRepository.findByPedidoIdPedido(
            		pedidoSalvo.getIdPedido(), pageable);
            Page<ItemPedido> doSegundo = itemPedidoRepository.findByPedidoIdPedido(
            		outroPedidoSalvo.getIdPedido(), pageable);

            assertThat(doPrimeiro).hasSize(1);
            assertThat(doSegundo).hasSize(1);
            assertThat(doPrimeiro.getContent().get(0).getIdItemPedido()).
            isNotEqualTo(doSegundo.getContent().get(0).getIdItemPedido());
        }
    }
    
 // ─── existsByPedidoUsuarioIdUsuarioAndProdutoIdProduto ───────────────────

    @Nested
    @DisplayName("existsByPedidoUsuarioIdUsuarioAndProdutoIdProduto")
    class ExistsByUsuarioAndProduto {

        @Test
        @DisplayName("deve retornar true quando usuário comprou o produto")
        void deveRetornarTrueQuandoUsuarioComprouProduto() {
            itemPedidoRepository.save(
                    buildItem(pedidoSalvo, produtoSalvo, 1, new BigDecimal("49.90")));
            em.flush();
            em.clear();

            boolean existe = itemPedidoRepository
                    .existsByPedidoUsuarioIdUsuarioAndProdutoIdProduto(
                            usuarioSalvo.getIdUsuario(),
                            produtoSalvo.getIdProduto());

            assertThat(existe).isTrue();
        }

        @Test
        @DisplayName("deve retornar false quando usuário nunca comprou o produto")
        void deveRetornarFalseQuandoUsuarioNaoComprouProduto() {
            boolean existe = itemPedidoRepository
                    .existsByPedidoUsuarioIdUsuarioAndProdutoIdProduto(
                            usuarioSalvo.getIdUsuario(),
                            produtoSalvo.getIdProduto());

            assertThat(existe).isFalse();
        }

        @Test
        @DisplayName("deve retornar false quando outro usuário comprou o produto mas não este")
        void deveRetornarFalseQuandoOutroUsuarioComprouProduto() {
            itemPedidoRepository.save(
                    buildItem(outroPedidoSalvo, produtoSalvo, 1, new BigDecimal("49.90")));
            em.flush();
            em.clear();

            boolean existe = itemPedidoRepository
                    .existsByPedidoUsuarioIdUsuarioAndProdutoIdProduto(
                            usuarioSalvo.getIdUsuario(),
                            produtoSalvo.getIdProduto());

            assertThat(existe).isFalse();
        }

        @Test
        @DisplayName("deve retornar false quando usuário comprou outro produto mas não este")
        void deveRetornarFalseQuandoUsuarioComprouOutroProduto() {
            itemPedidoRepository.save(
                    buildItem(pedidoSalvo, outroProdutoSalvo, 1, new BigDecimal("29.90")));
            em.flush();
            em.clear();

            boolean existe = itemPedidoRepository
                    .existsByPedidoUsuarioIdUsuarioAndProdutoIdProduto(
                            usuarioSalvo.getIdUsuario(),
                            produtoSalvo.getIdProduto());

            assertThat(existe).isFalse();
        }

        @Test
        @DisplayName("deve retornar true mesmo com múltiplos itens no pedido")
        void deveRetornarTrueComMultiplosItensDoPedido() {
            itemPedidoRepository.save(
                    buildItem(pedidoSalvo, outroProdutoSalvo, 2, new BigDecimal("29.90")));
            itemPedidoRepository.save(
                    buildItem(pedidoSalvo, produtoSalvo, 1, new BigDecimal("49.90")));
            em.flush();
            em.clear();

            boolean existe = itemPedidoRepository
                    .existsByPedidoUsuarioIdUsuarioAndProdutoIdProduto(
                            usuarioSalvo.getIdUsuario(),
                            produtoSalvo.getIdProduto());

            assertThat(existe).isTrue();
        }

        @Test
        @DisplayName("deve retornar false para ids inexistentes")
        void deveRetornarFalseParaIdsInexistentes() {
            boolean existe = itemPedidoRepository
                    .existsByPedidoUsuarioIdUsuarioAndProdutoIdProduto(999L, 999L);

            assertThat(existe).isFalse();
        }
    }
}