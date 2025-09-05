package com.serasa.desafio.controller;

import com.serasa.desafio.dto.PessoaRequestDto;
import com.serasa.desafio.dto.PessoaResponseDto;
import com.serasa.desafio.service.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaService service;

    public PessoaController(PessoaService service) {
        this.service = service;
    }

    @Operation(summary = "Cria uma nova pessoa", description = "Requer role ADMIN e validações do DTO")
    @ApiResponse(responseCode = "200", description = "Pessoa criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "403", description = "Acesso negado (sem permissão ADMIN)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PessoaResponseDto> criar(@Valid @RequestBody PessoaRequestDto request) {
        return ResponseEntity.ok(service.criar(request));
    }

    @Operation(summary = "Lista pessoas", description = "Pode filtrar por nome, idade e CEP. Retorna apenas pessoas ativas.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<Page<PessoaResponseDto>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer idade,
            @RequestParam(required = false) String cep,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.listar(nome, idade, cep, pageable));
    }

    @Operation(summary = "Busca pessoa por ID", description = "Retorna a pessoa correspondente ao ID informado.")
    @ApiResponse(responseCode = "200", description = "Pessoa encontrada")
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Atualiza uma pessoa existente", description = "Requer role ADMIN. Retorna 404 se a pessoa não existir.")
    @ApiResponse(responseCode = "200", description = "Pessoa atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PessoaResponseDto> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody PessoaRequestDto request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @Operation(summary = "Exclui pessoa logicamente", description = "Requer role ADMIN. Marca a pessoa como inativa em vez de excluir do banco.")
    @ApiResponse(responseCode = "204", description = "Pessoa excluída logicamente")
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluirLogicamente(id);
        return ResponseEntity.noContent().build();
    }
}
