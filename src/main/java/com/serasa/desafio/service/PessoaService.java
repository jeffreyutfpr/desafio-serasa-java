package com.serasa.desafio.service;

import com.serasa.desafio.dto.PessoaRequestDto;
import com.serasa.desafio.dto.PessoaResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PessoaService {

    PessoaResponseDto criar(PessoaRequestDto request);

    Page<PessoaResponseDto> listar(String nome, Integer idade, String cep, Pageable pageable);

    PessoaResponseDto atualizar(Long id, PessoaRequestDto request);

    void excluirLogicamente(Long id);

    PessoaResponseDto buscarPorId(Long id);
}

