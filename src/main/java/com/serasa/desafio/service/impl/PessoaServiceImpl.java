package com.serasa.desafio.service.impl;

import com.serasa.desafio.client.CepClient;
import com.serasa.desafio.dto.PessoaRequestDto;
import com.serasa.desafio.dto.PessoaResponseDto;
import com.serasa.desafio.entity.Endereco;
import com.serasa.desafio.entity.Pessoa;
import com.serasa.desafio.entity.Score;
import com.serasa.desafio.repository.PessoaRepository;
import com.serasa.desafio.service.PessoaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PessoaServiceImpl implements PessoaService {

    private final PessoaRepository repository;
    private final CepClient cepClient;

    private static final String PESSOA_NAO_ENCONTRADA = "Pessoa não encontrada";

    @Override
    public PessoaResponseDto criar(PessoaRequestDto dto) {
        Pessoa pessoa = Pessoa.builder()
                .nome(dto.getNome())
                .idade(dto.getIdade())
                .telefone(dto.getTelefone())
                .ativo(true)
                .build();

        pessoa.setScore(
                dto.getScore() != null
                        ? Score.builder().valor(dto.getScore()).build()
                        : Score.builder().valor(0).build()
        );

        pessoa.setEndereco(
                dto.getCep() != null
                        ? Endereco.builder().cep(normalizarCep(dto.getCep())).build()
                        : new Endereco()
        );

        preencherEnderecoPorCep(pessoa);

        return PessoaResponseDto.fromEntity(repository.save(pessoa));
    }

    @Override
    public PessoaResponseDto atualizar(Long id, PessoaRequestDto dto) {
        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(PESSOA_NAO_ENCONTRADA));

        pessoa.setNome(dto.getNome());
        pessoa.setIdade(dto.getIdade());
        pessoa.setTelefone(dto.getTelefone());

        if (pessoa.getScore() == null) {
            pessoa.setScore(Score.builder().valor(0).build());
        }
        pessoa.getScore().setValor(dto.getScore() != null ? dto.getScore() : 0);

        if (pessoa.getEndereco() == null) {
            pessoa.setEndereco(new Endereco());
        }
        pessoa.getEndereco().setCep(normalizarCep(dto.getCep()));

        preencherEnderecoPorCep(pessoa);

        return PessoaResponseDto.fromEntity(repository.save(pessoa));
    }

    @Override
    public void excluirLogicamente(Long id) {
        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(PESSOA_NAO_ENCONTRADA));
        pessoa.setAtivo(false);
        repository.save(pessoa);
    }

    @Override
    public Page<PessoaResponseDto> listar(String nome, Pageable pageable) {
        Page<Pessoa> page = repository.findByNomeContainingIgnoreCaseAndAtivoTrue(
                (nome == null ? "" : nome), pageable);
        return page.map(PessoaResponseDto::fromEntity);
    }

    @Override
    public Page<PessoaResponseDto> listar(String nome, Integer idade, String cep, Pageable pageable) {
        Page<Pessoa> page = repository.buscarComFiltros(
                (nome == null || nome.isBlank()) ? null : nome,
                idade,
                (cep == null || cep.isBlank()) ? null : normalizarCep(cep),
                pageable
        );
        return page.map(PessoaResponseDto::fromEntity);
    }

    @Override
    public PessoaResponseDto buscarPorId(Long id) {
        Pessoa pessoa = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(PESSOA_NAO_ENCONTRADA));
        return PessoaResponseDto.fromEntity(pessoa);
    }

    private void preencherEnderecoPorCep(Pessoa pessoa) {
        if (pessoa.getEndereco() == null || pessoa.getEndereco().getCep() == null) return;

        Map<String, Object> m = cepClient.buscaEndereco(pessoa.getEndereco().getCep());
        if (m == null || m.isEmpty()) return;

        Endereco e = pessoa.getEndereco();
        e.setCep(normalizarCep((String) m.get("cep")));
        e.setEstado((String) m.get("uf"));
        e.setCidade((String) m.get("localidade"));
        e.setBairro((String) m.get("bairro"));
        e.setLogradouro((String) m.get("logradouro"));
    }

    private String normalizarCep(String cep) {
        if (cep == null) return null;
        return cep.replaceAll("\\D", "");
    }
}
