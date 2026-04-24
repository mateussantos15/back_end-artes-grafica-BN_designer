package com.bndesigner.repository.categoria;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.bndesigner.domain.entity.categoria.Categoria;

@DataJpaTest
public class CategoriaRepositoryTest {
	
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Test
	void deveSalvarCategoriaComSucesso() {
		
		Categoria categoria = Categoria.builder()
				.nome("Camisas Religiosas")
				.descricao("Camisas de grupos de jovens, encontros comunitarios, pastorais comunitarias, círio de nazaré, etc...")
				.build();
		
		categoriaRepository.save(categoria);
		
		boolean existe = categoriaRepository.existsByNome("Camisas Religiosas");
		
		assertTrue(existe);
	}

}
