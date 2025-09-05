package com.serasa.desafio.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "pessoas")
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Integer idade;
    private String cep;
    private String estado;
    private String cidade;
    private String bairro;
    private String logradouro;
    private String telefone;
    private Integer score;
    private boolean ativo = true;

    public Pessoa() {
        // Construtor vazio necessário para o JPA/Hibernate instanciar a entidade.
    }

    @Transient
    public String getScoreDescricao() {
        if (score == null) return null;
        if (score <= 200) return "Insuficiente";
        if (score <= 500) return "Inaceitável";
        if (score <= 700) return "Aceitável";
        return "Recomendável";
    }
}
