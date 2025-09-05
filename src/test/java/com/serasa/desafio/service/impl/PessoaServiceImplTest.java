package com.serasa.desafio.service.impl;

import com.serasa.desafio.client.CepClient;
import com.serasa.desafio.dto.PessoaRequestDto;
import com.serasa.desafio.repository.PessoaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class PessoaServiceImplTest {

    @Test
    void deveTratarCepIncompletoSemQuebrar() {

        CepClient cepClient = Mockito.mock(CepClient.class);
        Map<String, Object> enderecoIncompleto = new HashMap<>();
        enderecoIncompleto.put("cep", "01001000");
        Mockito.when(cepClient.buscaEndereco("01001000")).thenReturn(enderecoIncompleto);

        PessoaRepository repoMock = Mockito.mock(PessoaRepository.class);
        Mockito.when(repoMock.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PessoaServiceImpl service = new PessoaServiceImpl(repoMock, cepClient);

        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Teste")
                .idade(25)
                .cep("01001000")
                .telefone("11999999999")
                .score(500)
                .build();

        assertDoesNotThrow(() -> service.criar(req));
    }

    @Test
    void deveLancarExcecaoAoAtualizarPessoaInexistente() {
        PessoaRepository repoMock = Mockito.mock(PessoaRepository.class);
        CepClient cepClient = Mockito.mock(CepClient.class);

        Mockito.when(repoMock.existsById(999L)).thenReturn(false);

        PessoaServiceImpl service = new PessoaServiceImpl(repoMock, cepClient);

        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Fulano")
                .idade(40)
                .cep("01001000")
                .telefone("11999999999")
                .score(600)
                .build();

        assertThrows(NoSuchElementException.class,
                () -> service.atualizar(999L, req));
    }

    @Test
    void deveLancarExcecaoAoExcluirPessoaInexistente() {
        PessoaRepository repoMock = Mockito.mock(PessoaRepository.class);
        CepClient cepClient = Mockito.mock(CepClient.class);

        Mockito.when(repoMock.findById(999L)).thenReturn(Optional.empty());

        PessoaServiceImpl service = new PessoaServiceImpl(repoMock, cepClient);

        assertThrows(NoSuchElementException.class,
                () -> service.excluirLogicamente(999L));
    }

    @Test
    void devePreencherEnderecoQuandoCepValido() {
        Map<String, Object> endereco = new HashMap<>();
        endereco.put("logradouro", "Praça da Sé");
        endereco.put("bairro", "Sé");
        endereco.put("localidade", "São Paulo");
        endereco.put("uf", "SP");

        CepClient cepClient = Mockito.mock(CepClient.class);
        Mockito.when(cepClient.buscaEndereco("01001000")).thenReturn(endereco);

        PessoaRepository repoMock = Mockito.mock(PessoaRepository.class);
        Mockito.when(repoMock.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PessoaServiceImpl service = new PessoaServiceImpl(repoMock, cepClient);

        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("João")
                .idade(30)
                .cep("01001000")
                .telefone("11988887777")
                .score(800)
                .build();

        var pessoa = service.criar(req);

        assertEquals("Praça da Sé", pessoa.getLogradouro());
        assertEquals("Sé", pessoa.getBairro());
        assertEquals("São Paulo", pessoa.getCidade());
        assertEquals("SP", pessoa.getEstado());
    }
}
