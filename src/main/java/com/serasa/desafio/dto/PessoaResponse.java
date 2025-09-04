package com.serasa.desafio.dto;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getScoreDescricao() { return scoreDescricao; }
    public void setScoreDescricao(String scoreDescricao) { this.scoreDescricao = scoreDescricao; }
}
