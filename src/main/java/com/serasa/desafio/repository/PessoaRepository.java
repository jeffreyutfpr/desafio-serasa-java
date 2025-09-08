package com.serasa.desafio.repository;

import com.serasa.desafio.entity.Pessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    Page<Pessoa> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome, Pageable pageable);

    @Query(value = """
            SELECT p FROM Pessoa p
            LEFT JOIN FETCH p.endereco
            LEFT JOIN FETCH p.score
            WHERE p.ativo = true
              AND (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
              AND (:idade IS NULL OR p.idade = :idade)
              AND (:cep IS NULL OR p.endereco.cep = :cep)
            """,
            countQuery = """
            SELECT COUNT(p) FROM Pessoa p
            WHERE p.ativo = true
              AND (:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
              AND (:idade IS NULL OR p.idade = :idade)
              AND (:cep IS NULL OR p.endereco.cep = :cep)
            """)
    Page<Pessoa> buscarComFiltros(String nome, Integer idade, String cep, Pageable pageable);
}
