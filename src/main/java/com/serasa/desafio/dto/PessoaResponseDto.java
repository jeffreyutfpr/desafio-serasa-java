package com.serasa.desafio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaResponseDto {
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
