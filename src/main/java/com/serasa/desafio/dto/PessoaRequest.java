package com.serasa.desafio.dto;

import jakarta.validation.constraints.*;

public class PessoaRequest {

    @NotBlank
    private String nome;

    @NotNull
    @Min(0)
    private Integer idade;

    @NotBlank
    private String cep;

    private String telefone;

    @NotNull
    @Min(0)
    @Max(1000)
    private Integer score;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}
