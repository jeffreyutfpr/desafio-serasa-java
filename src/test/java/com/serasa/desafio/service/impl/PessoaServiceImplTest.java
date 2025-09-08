package com.serasa.desafio.service.impl;

import com.serasa.desafio.client.CepClient;
import com.serasa.desafio.dto.PessoaRequestDto;
import com.serasa.desafio.dto.PessoaResponseDto;
import com.serasa.desafio.entity.Pessoa;
import com.serasa.desafio.repository.PessoaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PessoaServiceImplTest {

    @Mock
    private PessoaRepository repository;

    @Mock
    private CepClient cepClient;

    @InjectMocks
    private PessoaServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCriarPessoaMesmoComScoreENull() {
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Teste Null Score")
                .idade(25)
                .cep("01001000")
                .telefone("11999999999")
                .build();

        when(cepClient.buscaEndereco(anyString())).thenReturn(new HashMap<>());
        when(repository.save(any(Pessoa.class))).thenAnswer(invocation -> {
            Pessoa p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PessoaResponseDto resp = service.criar(req);

        assertNotNull(resp);
        assertEquals("Teste Null Score", resp.getNome());
        assertEquals(0, resp.getScore());
        assertEquals("Insuficiente", resp.getScoreDescricao());
    }

    @Test
    void deveCriarPessoaMesmoComCepNull() {
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Teste Null Cep")
                .idade(30)
                .score(500)
                .telefone("11888887777")
                .build();

        when(cepClient.buscaEndereco(anyString())).thenReturn(new HashMap<>());
        when(repository.save(any(Pessoa.class))).thenAnswer(invocation -> {
            Pessoa p = invocation.getArgument(0);
            p.setId(2L);
            return p;
        });

        PessoaResponseDto resp = service.criar(req);

        assertNotNull(resp);
        assertEquals("Teste Null Cep", resp.getNome());
        assertNull(resp.getCep());
    }

    @Test
    void deveAtualizarPessoaMesmoComScoreNullENoBanco() {
        Pessoa existente = Pessoa.builder()
                .id(10L)
                .nome("Antigo")
                .idade(40)
                .telefone("11777776666")
                .ativo(true)
                .build();
        existente.setScore(null);

        when(repository.findById(10L)).thenReturn(Optional.of(existente));
        when(cepClient.buscaEndereco(anyString())).thenReturn(new HashMap<>());
        when(repository.save(any(Pessoa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PessoaRequestDto updateReq = PessoaRequestDto.builder()
                .nome("Novo Nome")
                .idade(41)
                .cep("12345678")
                .telefone("11777776666")
                .score(700)
                .build();

        PessoaResponseDto resp = service.atualizar(10L, updateReq);

        assertNotNull(resp);
        assertEquals("Novo Nome", resp.getNome());
        assertEquals(700, resp.getScore());
        assertEquals("Aceitável", resp.getScoreDescricao());
    }

    @Test
    void deveAtualizarPessoaMesmoComEnderecoNullENoBanco() {
        Pessoa existente = Pessoa.builder()
                .id(11L)
                .nome("Maria")
                .idade(35)
                .telefone("11666665555")
                .ativo(true)
                .build();
        existente.setEndereco(null);

        when(repository.findById(11L)).thenReturn(Optional.of(existente));
        when(cepClient.buscaEndereco(anyString())).thenReturn(new HashMap<>());
        when(repository.save(any(Pessoa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PessoaRequestDto updateReq = PessoaRequestDto.builder()
                .nome("Maria Atualizada")
                .idade(36)
                .cep("87654321")
                .telefone("11666665555")
                .score(300)
                .build();

        PessoaResponseDto resp = service.atualizar(11L, updateReq);

        assertNotNull(resp);
        assertEquals("Maria Atualizada", resp.getNome());
        assertEquals("87654321", resp.getCep());
    }

    @Test
    void deveRetornarSemAlterarEnderecoQuandoViaCepNaoResponder() {
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("Sem ViaCep")
                .idade(40)
                .cep("00000000")
                .telefone("11900001111")
                .score(200)
                .build();

        when(cepClient.buscaEndereco(anyString())).thenReturn(new HashMap<>());
        when(repository.save(any(Pessoa.class))).thenAnswer(invocation -> {
            Pessoa p = invocation.getArgument(0);
            p.setId(99L);
            return p;
        });

        PessoaResponseDto resp = service.criar(req);

        assertNotNull(resp);
        assertEquals("Sem ViaCep", resp.getNome());
        assertEquals("00000000", resp.getCep());
        assertNull(resp.getCidade());
    }

    @Test
    void deveExcluirPessoaLogicamente() {
        Pessoa p = Pessoa.builder()
                .id(10L)
                .nome("Excluir Teste")
                .idade(22)
                .ativo(true)
                .build();

        when(repository.findById(10L)).thenReturn(Optional.of(p));
        when(repository.save(any(Pessoa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.excluirLogicamente(10L);

        assertFalse(p.isAtivo());
    }

    @Test
    void devePreencherEnderecoPorCepVazioSemErro() {
        PessoaRequestDto req = PessoaRequestDto.builder()
                .nome("SemEndereco")
                .idade(30)
                .cep("00000000")
                .score(200)
                .telefone("11999998888")
                .build();

        when(cepClient.buscaEndereco(anyString())).thenReturn(new HashMap<>());
        when(repository.save(any(Pessoa.class))).thenAnswer(invocation -> {
            Pessoa p = invocation.getArgument(0);
            p.setId(123L);
            return p;
        });

        PessoaResponseDto resp = service.criar(req);

        assertNotNull(resp);
        assertEquals("SemEndereco", resp.getNome());
        assertEquals("00000000", resp.getCep());
        assertNull(resp.getCidade());
    }

    @Test
    void deveListarSemFiltros() {
        Pageable pageable = PageRequest.of(0, 10);
        Pessoa p = Pessoa.builder()
                .id(1L)
                .nome("Teste")
                .idade(25)
                .ativo(true)
                .build();

        when(repository.findByNomeContainingIgnoreCaseAndAtivoTrue(anyString(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(p)));

        Page<PessoaResponseDto> page = service.listar(null, pageable);

        assertFalse(page.isEmpty());
        assertEquals("Teste", page.getContent().get(0).getNome());
    }

    @Test
    void deveLancarExcecaoQuandoBuscarPorIdInexistente() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.buscarPorId(999L));
    }

}
