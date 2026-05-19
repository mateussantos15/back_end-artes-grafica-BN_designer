package com.bndesigner.util;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bndesigner.exceptions.ResourceNotFoundException;

/**
 * Utilitário genérico para busca de entidades por ID em repositórios JPA.
 *
 * <p>Centraliza o padrão recorrente de {@code findById(...).orElseThrow(...)},
 * eliminando duplicação de código nos serviços da aplicação sem criar
 * acoplamento entre entidades de domínios distintos.</p>
 *
 * <h2>Exemplo de uso</h2>
 * <pre>{@code
 * Cupom cupom = EntityLookup.buscarOuLancar(cupomRepository, id, "Cupom");
 * }</pre>
 *
 * <p><strong>Nota:</strong> Esta classe não deve conter regras de negócio.
 * Seu único propósito é abstrair a infraestrutura de busca por ID.</p>
 */
public final class EntityLookup {

    /**
     * Construtor privado — classe utilitária, não deve ser instanciada.
     */
    private EntityLookup() {
        throw new UnsupportedOperationException(
                "EntityLookup é uma classe utilitária e não pode ser instanciada.");
    }

    /**
     * Busca uma entidade pelo seu ID no repositório informado.
     *
     * <p>Caso a entidade não seja encontrada, lança
     * {@link ResourceNotFoundException} com o nome da entidade e o ID
     * que foi pesquisado, para que o handler global produza uma resposta
     * HTTP 404 padronizada.</p>
     *
     * @param <T>           tipo da entidade gerenciada pelo repositório
     * @param repository    repositório JPA da entidade; não deve ser {@code null}
     * @param id            identificador da entidade a ser buscada; não deve ser {@code null}
     * @param nomeEntidade  nome legível da entidade, usado na mensagem de erro
     *                      (ex.: {@code "Cupom"}, {@code "Produto"})
     * @return              a entidade encontrada; nunca {@code null}
     * @throws ResourceNotFoundException se nenhum registro for encontrado para o ID informado
     */
    public static <T> T buscarOuLancar(
            JpaRepository<T, Long> repository,
            Long id,
            String nomeEntidade) {

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(nomeEntidade, id));
    }
}