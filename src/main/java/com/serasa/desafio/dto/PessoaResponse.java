package com.serasa.desafio.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PessoaResponse {
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
    private String scoreDescricao;

}
