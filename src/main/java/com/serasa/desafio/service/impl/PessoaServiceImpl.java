package com.serasa.desafio.service.impl;

import com.serasa.desafio.dto.PessoaRequestDto;
import com.serasa.desafio.dto.PessoaResponseDto;
import com.serasa.desafio.entity.Pessoa;
import com.serasa.desafio.repository.PessoaRepository;
import com.serasa.desafio.repository.PessoaSpecs;
import com.serasa.desafio.service.PessoaService;
import com.serasa.desafio.client.CepClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class PessoaServiceImpl implements PessoaService {

    private final PessoaRepository repository;
    private final CepClient cepClient;

    public PessoaServiceImpl(PessoaRepository repository, CepClient cepClient) {
        this.repository = repository;
        this.cepClient = cepClient;
    }

    @Override
    @Transactional
    public PessoaResponseDto criar(PessoaRequestDto req) {
        Pessoa p = new Pessoa();
        p.setNome(req.getNome());
        p.setIdade(req.getIdade());
        p.setCep(req.getCep());
        p.setTelefone(req.getTelefone());
        p.setScore(req.getScore());

        preencherEnderecoPorCep(p, req.getCep());

        p = repository.save(p);
        return toResponse(p);
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
        Pessoa p = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Pessoa não encontrada"));
        if (req.getNome() != null) p.setNome(req.getNome());
        if (req.getIdade() != null) p.setIdade(req.getIdade());
        if (req.getTelefone() != null) p.setTelefone(req.getTelefone());
        if (req.getScore() != null) p.setScore(req.getScore());
        if (req.getCep() != null && !req.getCep().equals(p.getCep())) {
            p.setCep(req.getCep());
            preencherEnderecoPorCep(p, req.getCep());
        }
        p = repository.save(p);
        return toResponse(p);
    }

    @Override
    @Transactional
    public void excluirLogicamente(Long id) {
        Pessoa p = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Pessoa não encontrada"));
        p.setAtivo(false);
        repository.save(p);
    }

    @Override
    public PessoaResponseDto buscarPorId(Long id) {
        Pessoa p = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Pessoa não encontrada"));
        return toResponse(p);
    }

    private void preencherEnderecoPorCep(Pessoa p, String cep) {
        Map<String, Object> endereco = cepClient.buscaEndereco(cep);
        if (endereco != null && endereco.get("erro") == null) {
            p.setEstado((String) endereco.getOrDefault("uf", null));
            p.setCidade((String) endereco.getOrDefault("localidade", null));
            p.setBairro((String) endereco.getOrDefault("bairro", null));
            p.setLogradouro((String) endereco.getOrDefault("logradouro", null));
        }
    }

    private PessoaResponseDto toResponse(Pessoa p) {
        PessoaResponseDto r = new PessoaResponseDto();
        r.setId(p.getId());
        r.setNome(p.getNome());
        r.setIdade(p.getIdade());
        r.setCep(p.getCep());
        r.setEstado(p.getEstado());
        r.setCidade(p.getCidade());
        r.setBairro(p.getBairro());
        r.setLogradouro(p.getLogradouro());
        r.setTelefone(p.getTelefone());
        r.setScore(p.getScore());
        r.setScoreDescricao(p.getScoreDescricao());
        return r;
    }
}
