package com.serasa.desafio.service.impl;

import com.serasa.desafio.client.CepClient;
import com.serasa.desafio.dto.PessoaRequestDto;
import com.serasa.desafio.dto.PessoaResponseDto;
import com.serasa.desafio.entity.Pessoa;
import com.serasa.desafio.repository.PessoaRepository;
import com.serasa.desafio.repository.PessoaSpecs;
import com.serasa.desafio.service.PessoaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class PessoaServiceImpl implements PessoaService {

    private static final String PESSOA_NAO_ENCONTRADA = "Pessoa não encontrada";

    private final PessoaRepository repository;
    private final CepClient cepClient;

    public PessoaServiceImpl(PessoaRepository repository, CepClient cepClient) {
        this.repository = repository;
        this.cepClient = cepClient;
    }

    @Override
    @Transactional
    public PessoaResponseDto criar(PessoaRequestDto req) {
        Pessoa pessoa = toEntity(req);
        preencherEnderecoPorCep(pessoa, req.getCep());
        pessoa.setAtivo(true);
        pessoa = repository.save(pessoa);
        return toResponse(pessoa);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PessoaResponseDto> listar(String nome, Integer idade, String cep, Pageable pageable) {
        Specification<Pessoa> spec = Specification.where(PessoaSpecs.ativoTrue())
                .and(PessoaSpecs.nomeContains(nome))
                .and(PessoaSpecs.idadeEquals(idade))
                .and(PessoaSpecs.cepEquals(cep));
        return repository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public PessoaResponseDto atualizar(Long id, PessoaRequestDto req) {
        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(PESSOA_NAO_ENCONTRADA));

        if (req.getNome() != null) pessoa.setNome(req.getNome());
        if (req.getIdade() != null) pessoa.setIdade(req.getIdade());
        if (req.getTelefone() != null) pessoa.setTelefone(req.getTelefone());
        if (req.getScore() != null) pessoa.setScore(req.getScore());
        if (req.getCep() != null && !req.getCep().equals(pessoa.getCep())) {
            pessoa.setCep(req.getCep());
            preencherEnderecoPorCep(pessoa, req.getCep());
        }

        pessoa = repository.save(pessoa);
        return toResponse(pessoa);
    }

    @Override
    @Transactional
    public void excluirLogicamente(Long id) {
        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(PESSOA_NAO_ENCONTRADA));
        pessoa.setAtivo(false);
        repository.save(pessoa);
    }

    @Override
    @Transactional(readOnly = true)
    public PessoaResponseDto buscarPorId(Long id) {
        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(PESSOA_NAO_ENCONTRADA));
        return toResponse(pessoa);
    }

    private Pessoa toEntity(PessoaRequestDto req) {
        Pessoa p = new Pessoa();
        p.setNome(req.getNome());
        p.setIdade(req.getIdade());
        p.setCep(req.getCep());
        p.setTelefone(req.getTelefone());
        p.setScore(req.getScore());
        p.setAtivo(true);
        return p;
    }

    private PessoaResponseDto toResponse(Pessoa p) {
        return PessoaResponseDto.builder()
                .id(p.getId())
                .nome(p.getNome())
                .idade(p.getIdade())
                .cep(p.getCep())
                .estado(p.getEstado())
                .cidade(p.getCidade())
                .bairro(p.getBairro())
                .logradouro(p.getLogradouro())
                .telefone(p.getTelefone())
                .score(p.getScore())
                .scoreDescricao(calcularScoreDescricao(p.getScore()))
                .build();
    }

    private void preencherEnderecoPorCep(Pessoa p, String cep) {
        Map<String, Object> endereco = cepClient.buscaEndereco(cep);

        if (!endereco.isEmpty() && endereco.get("erro") == null) {
            p.setEstado((String) endereco.getOrDefault("uf", null));
            p.setCidade((String) endereco.getOrDefault("localidade", null));
            p.setBairro((String) endereco.getOrDefault("bairro", null));
            p.setLogradouro((String) endereco.getOrDefault("logradouro", null));
        }
    }

    private String calcularScoreDescricao(Integer score) {
        if (score == null) return null;
        if (score <= 200) return "Baixo";
        if (score <= 500) return "Inaceitável";
        if (score <= 700) return "Aceitável";
        return "Recomendável";
    }
}
