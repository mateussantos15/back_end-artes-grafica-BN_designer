package com.bndesigner.repository.arquivo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.bndesigner.domain.entity.arquivo.Arquivo;

@DataJpaTest
@DisplayName("ArquivoRepository — Testes de Persistência")
class ArquivoRepositoryTest {
 
    @Autowired
    private ArquivoRepository repository;
 
    private Arquivo ativo1;
    private Arquivo ativo2;
    private Arquivo inativo;
 
    @BeforeEach
    void setUp() {
        repository.deleteAll();
 
        ativo1 = new Arquivo();
        ativo1.setCaminhoArquivo("/uploads/a.pdf");
        ativo1.setHashArquivo("hash-a");
        ativo1.setAtivo(true);
 
        ativo2 = new Arquivo();
        ativo2.setCaminhoArquivo("/uploads/b.pdf");
        ativo2.setHashArquivo("hash-b");
        ativo2.setAtivo(true);
 
        inativo = new Arquivo();
        inativo.setCaminhoArquivo("/uploads/c.pdf");
        inativo.setHashArquivo("hash-c");
        inativo.setAtivo(false);
 
        repository.saveAll(List.of(ativo1, ativo2, inativo));
    }
 
    // ─── findByAtivoTrue() ──────────────────────────────────────────────────────
 
    @Test
    @DisplayName("findByAtivoTrue() — deve retornar apenas arquivos com ativo=true")
    void findByAtivoTrue_retornaSomenteAtivos() {
    	Pageable pageable = PageRequest.of(0, 10);
        Page<Arquivo> ativos = repository.findByAtivoTrue(pageable);
 
        assertThat(ativos).hasSize(2);
        assertThat(ativos).extracting(Arquivo::getAtivo).containsOnly(true);
        assertThat(ativos).extracting(Arquivo::getCaminhoArquivo)
                .containsExactlyInAnyOrder("/uploads/a.pdf", "/uploads/b.pdf");
    }
 
    @Test
    @DisplayName("findByAtivoTrue() — deve retornar lista vazia quando todos estão inativos")
    void findByAtivoTrue_todosInativos_retornaVazio() {
        repository.findAll().forEach(a -> {
            a.setAtivo(false);
            repository.save(a);
        });
 
        Pageable pageable = PageRequest.of(0, 10);
        assertThat(repository.findByAtivoTrue(pageable)).isEmpty();
    }
 
    // ─── findByHashArquivo() ────────────────────────────────────────────────────
 
    @Test
    @DisplayName("findByHashArquivo() — deve retornar o arquivo com o hash informado")
    void findByHashArquivo_hashExistente_retornaArquivo() {
        Optional<Arquivo> result = repository.findByHashArquivo("hash-a");
 
        assertThat(result).isPresent();
        assertThat(result.get().getCaminhoArquivo()).isEqualTo("/uploads/a.pdf");
    }
 
    @Test
    @DisplayName("findByHashArquivo() — deve retornar Optional vazio quando hash não existe")
    void findByHashArquivo_hashInexistente_retornaVazio() {
        Optional<Arquivo> result = repository.findByHashArquivo("hash-inexistente");
 
        assertThat(result).isEmpty();
    }
 
    // ─── existsByHashArquivo() ──────────────────────────────────────────────────
 
    @Test
    @DisplayName("existsByHashArquivo() — deve retornar true quando hash já está cadastrado")
    void existsByHashArquivo_hashExistente_retornaTrue() {
        assertThat(repository.existsByHashArquivo("hash-b")).isTrue();
    }
 
    @Test
    @DisplayName("existsByHashArquivo() — deve retornar false quando hash não existe")
    void existsByHashArquivo_hashInexistente_retornaFalse() {
        assertThat(repository.existsByHashArquivo("hash-xyz")).isFalse();
    }
 
    // ─── @PrePersist / defaults ─────────────────────────────────────────────────
 
    @Test
    @DisplayName("save() — deve preencher dataUpload automaticamente via @PrePersist")
    void save_semDataUpload_preencheAutomaticamente() {
        Arquivo novo = new Arquivo();
        novo.setCaminhoArquivo("/uploads/novo.pdf");
 
        Arquivo salvo = repository.save(novo);
 
        assertThat(salvo.getDataUpload()).isNotNull();
    }
 
    @Test
    @DisplayName("save() — deve setar ativo=true por padrão via @PrePersist")
    void save_semAtivo_setaAtivoTrue() {
        Arquivo novo = new Arquivo();
        novo.setCaminhoArquivo("/uploads/novo.pdf");
 
        Arquivo salvo = repository.save(novo);
 
        assertThat(salvo.getAtivo()).isTrue();
    }
 
    // ─── operações básicas CRUD ─────────────────────────────────────────────────
 
    @Test
    @DisplayName("findById() — deve encontrar arquivo por ID após salvar")
    void findById_idExistente_retornaArquivo() {
        Optional<Arquivo> result = repository.findById(ativo1.getIdArquivo());
 
        assertThat(result).isPresent();
        assertThat(result.get().getHashArquivo()).isEqualTo("hash-a");
    }
 
    @Test
    @DisplayName("save() — deve persistir soft delete (ativo=false) corretamente")
    void save_softDelete_persisteAtivoFalso() {
        ativo1.setAtivo(false);
        repository.save(ativo1);
 
        Arquivo atualizado = repository.findById(ativo1.getIdArquivo()).orElseThrow();
        assertThat(atualizado.getAtivo()).isFalse();
    }
}

