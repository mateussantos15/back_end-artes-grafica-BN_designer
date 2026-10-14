package com.bndesigner.repository.avaliacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.bndesigner.config.JpaAuditingConfig;
import com.bndesigner.domain.entity.avaliacao.Avaliacao;
import com.bndesigner.domain.entity.produto.Produto;
import com.bndesigner.domain.entity.usuario.Usuario;
import com.bndesigner.domain.enums.usuario.TipoUsuario;
import com.bndesigner.repository.produto.ProdutoRepository;
import com.bndesigner.repository.usuario.UsuarioRepository;

import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class AvaliacaoRepositoryTest {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;
    
    private Usuario usuarioCriado;
    private Produto produtoCriado;
    
    @BeforeEach
    void setUp() {
    	
    	usuarioCriado = usuarioRepository.save(Usuario.builder()
         		.nome("Mateus")
         		.email("mateus@email.com")
         		.senha("hash_da_senha")
         		.tipoUsuario(TipoUsuario.CLIENTE)
         		.build()
    	);
    	
		produtoCriado = produtoRepository.save(Produto.builder()
				.titulo("Produto Teste")
				.descricao("Descrição do Produto Teste")
				.preco(new BigDecimal("100.0"))
				.build()
		);
    }

    @Test
    void deveSalvarEBuscarAvaliacao() {

        Avaliacao avaliacao = Avaliacao.builder()
                .usuario(usuarioCriado)
                .produto(produtoCriado)
                .nota(5)
                .comentario("Excelente produto!")
                .build();

        Avaliacao salva = avaliacaoRepository.save(avaliacao);

        assertNotNull(salva.getIdAvaliacao());

        Optional<Avaliacao> encontrada =
                avaliacaoRepository.findById(salva.getIdAvaliacao());

        assertTrue(encontrada.isPresent());
        assertEquals(5, encontrada.get().getNota());
        assertEquals("Excelente produto!", encontrada.get().getComentario());
        assertEquals(
                usuarioCriado.getIdUsuario(),
                encontrada.get().getUsuario().getIdUsuario());
        assertEquals(
                produtoCriado.getIdProduto(),
                encontrada.get().getProduto().getIdProduto());
    }

    @Test
    void deveEncontrarAvaliacaoPorUsuarioEProduto() {

        Avaliacao avaliacao = avaliacaoRepository.save(
                Avaliacao.builder()
                        .usuario(usuarioCriado)
                        .produto(produtoCriado)
                        .nota(4)
                        .build()
        );

        Optional<Avaliacao> encontrada =
                avaliacaoRepository
                        .findByUsuarioIdUsuarioAndProdutoIdProduto(
                                usuarioCriado.getIdUsuario(),
                                produtoCriado.getIdProduto());

        assertTrue(encontrada.isPresent());
        assertEquals(
                avaliacao.getIdAvaliacao(),
                encontrada.get().getIdAvaliacao());
    }

    @Test
    void deveRetornarTrueQuandoUsuarioJaAvaliouProduto() {

        avaliacaoRepository.save(
                Avaliacao.builder()
                        .usuario(usuarioCriado)
                        .produto(produtoCriado)
                        .nota(5)
                        .build()
        );

        boolean existe =
                avaliacaoRepository
                        .existsByUsuarioIdUsuarioAndProdutoIdProduto(
                                usuarioCriado.getIdUsuario(),
                                produtoCriado.getIdProduto());

        assertTrue(existe);
    }

    @Test
    void deveRetornarFalseQuandoUsuarioNaoAvaliouProduto() {

        boolean existe =
                avaliacaoRepository
                        .existsByUsuarioIdUsuarioAndProdutoIdProduto(
                                usuarioCriado.getIdUsuario(),
                                produtoCriado.getIdProduto());

        assertFalse(existe);
    }

    @Test
    void deveListarAvaliacoesPorProduto() {

        avaliacaoRepository.save(
                Avaliacao.builder()
                        .usuario(usuarioCriado)
                        .produto(produtoCriado)
                        .nota(5)
                        .build()
        );

        Page<Avaliacao> resultado =
                avaliacaoRepository.findByProdutoIdProduto(
                        produtoCriado.getIdProduto(),
                        PageRequest.of(0, 10));

        assertEquals(1, resultado.getTotalElements());
        assertEquals(5, resultado.getContent().get(0).getNota());
    }
}