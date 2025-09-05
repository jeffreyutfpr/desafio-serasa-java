package com.serasa.desafio.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PessoaTest {

    @Test
    void deveTestarGettersESetters() {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(99L);
        pessoa.setNome("Teste");
        pessoa.setIdade(45);
        pessoa.setCep("12345678");
        pessoa.setEstado("SP");
        pessoa.setCidade("São Paulo");
        pessoa.setBairro("Centro");
        pessoa.setLogradouro("Rua A");
        pessoa.setTelefone("11987654321");
        pessoa.setScore(750);
        pessoa.setAtivo(false);

        assertEquals(99L, pessoa.getId());
        assertEquals("Teste", pessoa.getNome());
        assertEquals(45, pessoa.getIdade());
        assertEquals("12345678", pessoa.getCep());
        assertEquals("SP", pessoa.getEstado());
        assertEquals("São Paulo", pessoa.getCidade());
        assertEquals("Centro", pessoa.getBairro());
        assertEquals("Rua A", pessoa.getLogradouro());
        assertEquals("11987654321", pessoa.getTelefone());
        assertEquals(750, pessoa.getScore());
        assertFalse(pessoa.isAtivo());
    }

    @Test
    void deveUsarEqualsAndHashCode() {
        Pessoa p1 = new Pessoa(
                1L, "Maria", 30, "01001000", "SP", "São Paulo",
                "Centro", "Rua B", "11911111111", 600, true
        );
        Pessoa p2 = new Pessoa(
                1L, "Maria", 30, "01001000", "SP", "São Paulo",
                "Centro", "Rua B", "11911111111", 600, true
        );

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());

        p2.setId(2L);
        assertNotEquals(p1, p2);
    }

    @Test
    void deveCriarPessoaComAllArgsConstructor() {
        Pessoa pessoa = new Pessoa(
                10L, "João", 30, "01001000", "SP", "São Paulo",
                "Sé", "Praça da Sé", "11999999999", 900, true
        );

        assertEquals(10L, pessoa.getId());
        assertEquals("João", pessoa.getNome());
        assertEquals(30, pessoa.getIdade());
        assertEquals("01001000", pessoa.getCep());
        assertEquals("SP", pessoa.getEstado());
        assertEquals("São Paulo", pessoa.getCidade());
        assertEquals("Sé", pessoa.getBairro());
        assertEquals("Praça da Sé", pessoa.getLogradouro());
        assertEquals("11999999999", pessoa.getTelefone());
        assertEquals(900, pessoa.getScore());
        assertTrue(pessoa.isAtivo());
    }

    @Test
    void deveRetornarScoreDescricaoCorretamente() {
        Pessoa pessoa = new Pessoa();

        pessoa.setScore(null);
        assertNull(pessoa.getScoreDescricao());

        pessoa.setScore(150);
        assertEquals("Insuficiente", pessoa.getScoreDescricao());

        pessoa.setScore(400);
        assertEquals("Inaceitável", pessoa.getScoreDescricao());

        pessoa.setScore(650);
        assertEquals("Aceitável", pessoa.getScoreDescricao());

        pessoa.setScore(800);
        assertEquals("Recomendável", pessoa.getScoreDescricao());
    }
}
