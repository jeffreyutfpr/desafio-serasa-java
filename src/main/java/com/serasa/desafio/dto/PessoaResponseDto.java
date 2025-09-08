package com.serasa.desafio.dto;

import com.serasa.desafio.entity.Endereco;
import com.serasa.desafio.entity.Pessoa;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaResponseDto {

    private Long id;
    private String nome;
    private Integer idade;
    private String telefone;

    private Integer score;
    private String scoreDescricao;

    private String cep;
    private String estado;
    private String cidade;
    private String bairro;
    private String logradouro;

    public static PessoaResponseDto fromEntity(Pessoa pessoa) {
        Integer valor = null;
        String desc = null;
        if (pessoa.getScore() != null) {
            valor = pessoa.getScore().getValor();
            desc = pessoa.getScore().getDescricao();
        }

        Endereco e = pessoa.getEndereco();

        return PessoaResponseDto.builder()
                .id(pessoa.getId())
                .nome(pessoa.getNome())
                .idade(pessoa.getIdade())
                .telefone(pessoa.getTelefone())
                .score(valor)
                .scoreDescricao(desc)
                .cep(e != null ? e.getCep() : null)
                .estado(e != null ? e.getEstado() : null)
                .cidade(e != null ? e.getCidade() : null)
                .bairro(e != null ? e.getBairro() : null)
                .logradouro(e != null ? e.getLogradouro() : null)
                .build();
    }
}
