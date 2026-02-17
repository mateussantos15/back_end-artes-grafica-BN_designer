package com.bndesigner.repository.categoria;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.bndesigner.domain.entity.categoria.Categoria;

@DataJpaTest
@ActiveProfiles("test")
class CategoriaRepositoryTest {
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Test
	void deveSalvarCategoriaComSucesso() {
		Categoria categoria = Categoria.builder()
				.nome("Cartões")
				.descricao("Cartões de Visita")
				.ativa(true)
				.build();
		
		Categoria salva = categoriaRepository.save(categoria);
		
		assertNotNull(salva.getIdCategoria());
	}
	
	@Test
	void deveVerificarSeNomeExiste() {
		Categoria categoria = Categoria.builder()
				.nome("Banners")
				.ativa(true)
				.build();
		
		categoriaRepository.save(categoria);
		
		boolean existe = categoriaRepository.existsByNome("Banners");
		
		assertTrue(existe);
	}
}
