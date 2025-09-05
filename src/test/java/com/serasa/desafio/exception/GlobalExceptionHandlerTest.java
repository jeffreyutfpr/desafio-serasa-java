package com.serasa.desafio.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveRetornarNotFound() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleNotFound(new java.util.NoSuchElementException("Pessoa não encontrada"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Recurso não encontrado", response.getBody().get("error"));
    }

    @Test
    void deveRetornarErroDeValidacao() {

        class PessoaFake {
            private String nome;
            public String getNome() { return nome; }
            public void setNome(String nome) { this.nome = nome; }
        }

        PessoaFake alvo = new PessoaFake();

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(alvo, "pessoa");
        bindingResult.rejectValue("nome", "NotBlank", "Nome é obrigatório");

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro de validação", response.getBody().get("error"));
        assertTrue(((Map<?, ?>) response.getBody().get("details")).containsKey("nome"));
        assertEquals("Nome é obrigatório",
                ((Map<?, ?>) response.getBody().get("details")).get("nome"));
    }

    @Test
    void deveRetornarAccessDenied() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleAccessDenied(new org.springframework.security.access.AccessDeniedException("Sem permissão"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Acesso negado", response.getBody().get("error"));
    }

    @Test
    void deveRetornarErroGenerico() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleGeneric(new RuntimeException("Falha inesperada"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno", response.getBody().get("error"));
    }
}
