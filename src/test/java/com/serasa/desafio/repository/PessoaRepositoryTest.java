package com.serasa.desafio.repository;

import com.serasa.desafio.entity.Endereco;
import com.serasa.desafio.entity.Pessoa;
import com.serasa.desafio.entity.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class PessoaRepositoryTest {

    @Autowired
    private PessoaRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    private Pessoa criarPessoa(String nome, Integer idade, String cep, Integer score) {
        Pessoa pessoa = Pessoa.builder()
                .nome(nome)
                .idade(idade)
                .ativo(true)
                .telefone("11999999999")
                .endereco(Endereco.builder().cep(cep).build())
                .score(Score.builder().valor(score).build())
                .build();
        return repository.save(pessoa);
    }

    @Test
    void deveRetornarTodosQuandoSemFiltros() {
        criarPessoa("João", 20, "01001000", 500);
        criarPessoa("Maria", 30, "02002000", 600);

        Page<Pessoa> page = repository.buscarComFiltros(
                null, null, null, PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements());
    }

    @Test
    void deveFiltrarPorNomeIgnorandoCase() {
        criarPessoa("Carlos", 40, "03003000", 700);
        criarPessoa("carlos almeida", 25, "04004000", 300);

        Page<Pessoa> page = repository.buscarComFiltros(
                "carlos", null, null, PageRequest.of(0, 10));

        assertFalse(page.isEmpty());
        List<Pessoa> pessoas = page.getContent();
        assertTrue(pessoas.stream().allMatch(p -> p.getNome().toLowerCase().contains("carlos")));
    }

    @Test
    void deveFiltrarPorIdade() {
        criarPessoa("Ana", 25, "05005000", 400);
        criarPessoa("Pedro", 33, "06006000", 200);

        Page<Pessoa> page = repository.buscarComFiltros(
                null, 25, null, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals(25, page.getContent().get(0).getIdade());
    }

    @Test
    void deveFiltrarPorCep() {
        criarPessoa("Fernanda", 22, "07007000", 800);
        criarPessoa("Roberto", 44, "08008000", 500);

        Page<Pessoa> page = repository.buscarComFiltros(
                null, null, "07007000", PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("07007000", page.getContent().get(0).getEndereco().getCep());
    }

    @Test
    void naoDeveTrazerInativos() {
        Pessoa ativo = criarPessoa("Ativo", 20, "09009000", 100);
        Pessoa inativo = criarPessoa("Inativo", 30, "10010000", 200);
        inativo.setAtivo(false);
        repository.save(inativo);

        Page<Pessoa> page = repository.buscarComFiltros(
                null, null, null, PageRequest.of(0, 10));

        assertTrue(page.stream().allMatch(Pessoa::isAtivo));
        assertFalse(page.stream().anyMatch(p -> !p.isAtivo()));
    }
}
