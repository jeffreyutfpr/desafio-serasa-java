package com.serasa.desafio.controller;

import com.serasa.desafio.dto.PessoaRequest;
import com.serasa.desafio.dto.PessoaResponse;
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
    public ResponseEntity<PessoaResponse> criar(@Validated @RequestBody PessoaRequest request) {
        return ResponseEntity.ok(service.criarPessoa(request));
    }

    @GetMapping
    public ResponseEntity<Page<PessoaResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer idade,
            @RequestParam(required = false) String cep,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.listarPessoas(nome, idade, cep, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PessoaResponse> atualizar(@PathVariable Long id, @RequestBody PessoaRequest request) {
        return ResponseEntity.ok(service.atualizarPessoa(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluirPessoa(id);
        return ResponseEntity.noContent().build();
    }
}
