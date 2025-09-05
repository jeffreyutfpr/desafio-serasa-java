package com.serasa.desafio.controller;

import com.serasa.desafio.dto.PessoaRequestDto;
import com.serasa.desafio.dto.PessoaResponseDto;
import com.serasa.desafio.service.PessoaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaService service;

    public PessoaController(PessoaService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PessoaResponseDto> criar(@Validated @RequestBody PessoaRequestDto request) {
        return ResponseEntity.ok(service.criar(request));
    }

    @GetMapping
    public ResponseEntity<Page<PessoaResponseDto>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer idade,
            @RequestParam(required = false) String cep,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.listar(nome, idade, cep, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PessoaResponseDto> atualizar(@PathVariable Long id, @RequestBody PessoaRequestDto request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluirLogicamente(id);
        return ResponseEntity.noContent().build();
    }
}
