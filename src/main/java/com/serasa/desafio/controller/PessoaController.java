package com.serasa.desafio.controller;

import com.serasa.desafio.dto.PessoaRequestDto;
import com.serasa.desafio.dto.PessoaResponseDto;
import com.serasa.desafio.service.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaService service;

    public PessoaController(PessoaService service) {
        this.service = service;
    }

    @Operation(summary = "Cria uma nova pessoa", description = "Requer role ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pessoa criada com sucesso",
                    headers = { @Header(name = "Location", description = "URL do recurso criado") }),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PessoaResponseDto> criar(@Valid @RequestBody PessoaRequestDto request) {
        PessoaResponseDto resp = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resp.getId())
                .toUri();
        return ResponseEntity.created(location).body(resp);
    }

    @Operation(summary = "Lista pessoas", description = "Pode filtrar por nome, idade e CEP. Requer role USER ou ADMIN")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Page<PessoaResponseDto>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer idade,
            @RequestParam(required = false) String cep,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.listar(nome, idade, cep, pageable));
    }

    @Operation(summary = "Busca pessoa por ID", description = "Requer role USER ou ADMIN")
    @ApiResponse(responseCode = "200", description = "Pessoa encontrada")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PessoaResponseDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Atualiza uma pessoa existente", description = "Requer role ADMIN")
    @ApiResponse(responseCode = "200", description = "Pessoa atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PessoaResponseDto> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody PessoaRequestDto request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @Operation(summary = "Exclui pessoa logicamente", description = "Requer role ADMIN. Marca a pessoa como inativa.")
    @ApiResponse(responseCode = "204", description = "Pessoa excluída logicamente")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    @ApiResponse(responseCode = "404", description = "Pessoa não encontrada")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluirLogicamente(id);
        return ResponseEntity.noContent().build();
    }
}
