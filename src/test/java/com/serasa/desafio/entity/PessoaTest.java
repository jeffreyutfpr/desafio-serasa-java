package com.serasa.desafio.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PessoaTest {

    @Test
    void deveTestarGettersESettersComModelagemNova() {

        Endereco endereco = Endereco.builder()
                .cep("12345678")
                .estado("SP")
                .cidade("São Paulo")
                .bairro("Centro")
                .logradouro("Rua A, 123")
                .build();

        Score score = Score.builder()
                .valor(150)
                .build();

        Pessoa pessoa = Pessoa.builder()
                .id(99L)
                .nome("Teste")
                .idade(45)
                .telefone("11999999999")
                .ativo(true)
                .endereco(endereco)
                .score(score)
                .build();

        assertEquals(99L, pessoa.getId());
        assertEquals("Teste", pessoa.getNome());
        assertEquals(45, pessoa.getIdade());
        assertEquals("11999999999", pessoa.getTelefone());
        assertTrue(pessoa.isAtivo());

        assertNotNull(pessoa.getEndereco());
        assertEquals("12345678", pessoa.getEndereco().getCep());
        assertEquals("SP", pessoa.getEndereco().getEstado());
        assertEquals("São Paulo", pessoa.getEndereco().getCidade());
        assertEquals("Centro", pessoa.getEndereco().getBairro());
        assertEquals("Rua A, 123", pessoa.getEndereco().getLogradouro());

        assertNotNull(pessoa.getScore());
        assertEquals(150, pessoa.getScore().getValor());
        assertEquals("Insuficiente", pessoa.getScore().getDescricao());

        pessoa.getScore().setValor(400);
        assertEquals("Inaceitável", pessoa.getScore().getDescricao());

        pessoa.getScore().setValor(650);
        assertEquals("Aceitável", pessoa.getScore().getDescricao());

        pessoa.getScore().setValor(800);
        assertEquals("Recomendável", pessoa.getScore().getDescricao());
    }

    @Test
    void devePermitirAtualizarEnderecoEscore() {
        Pessoa pessoa = Pessoa.builder()
                .nome("Maria")
                .idade(30)
                .telefone("11988887777")
                .ativo(true)
                .build();

        pessoa.setEndereco(Endereco.builder().cep("01001000").build());
        assertEquals("01001000", pessoa.getEndereco().getCep());

        pessoa.getEndereco().setCidade("São Paulo");
        pessoa.getEndereco().setEstado("SP");
        pessoa.getEndereco().setBairro("Sé");
        pessoa.getEndereco().setLogradouro("Praça da Sé, 1");
        assertEquals("São Paulo", pessoa.getEndereco().getCidade());
        assertEquals("SP", pessoa.getEndereco().getEstado());
        assertEquals("Sé", pessoa.getEndereco().getBairro());
        assertEquals("Praça da Sé, 1", pessoa.getEndereco().getLogradouro());

        pessoa.setScore(Score.builder().valor(500).build());
        assertEquals(500, pessoa.getScore().getValor());
        assertEquals("Inaceitável", pessoa.getScore().getDescricao());

        pessoa.getScore().setValor(710);
        assertEquals("Recomendável", pessoa.getScore().getDescricao());
    }
}
