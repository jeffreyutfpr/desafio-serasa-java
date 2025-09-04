package com.serasa.desafio.repository;

import com.serasa.desafio.entity.Pessoa;
import org.springframework.data.jpa.domain.Specification;

public class PessoaSpecs {

    public static Specification<Pessoa> ativoTrue() {
        return (root, query, cb) -> cb.isTrue(root.get("ativo"));
    }

    public static Specification<Pessoa> nomeContains(String nome) {
        return (root, query, cb) -> nome == null ? cb.conjunction() :
                cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Pessoa> idadeEquals(Integer idade) {
        return (root, query, cb) -> idade == null ? cb.conjunction() :
                cb.equal(root.get("idade"), idade);
    }

    public static Specification<Pessoa> cepEquals(String cep) {
        return (root, query, cb) -> cep == null ? cb.conjunction() :
                cb.equal(root.get("cep"), cep);
    }
}
