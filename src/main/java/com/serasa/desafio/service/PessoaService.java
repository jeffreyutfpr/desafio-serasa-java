package com.serasa.desafio.service;

import com.serasa.desafio.dto.PessoaRequestDto;
import com.serasa.desafio.dto.PessoaResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PessoaService {

    PessoaResponseDto criar(PessoaRequestDto dto);

    PessoaResponseDto atualizar(Long id, PessoaRequestDto dto);

    void excluirLogicamente(Long id);

    Page<PessoaResponseDto> listar(String nome, Pageable pageable);

    Page<PessoaResponseDto> listar(String nome, Integer idade, String cep, Pageable pageable);

    PessoaResponseDto buscarPorId(Long id);
}
