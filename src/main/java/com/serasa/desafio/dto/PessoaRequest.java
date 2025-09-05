package com.serasa.desafio.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PessoaRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Idade é obrigatória")
    @Min(value = 0, message = "Idade deve ser maior ou igual a 0")
    @Max(value = 120, message = "Idade deve ser menor ou igual a 120")
    private Integer idade;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^\\d{8}$", message = "CEP deve ter exatamente 8 dígitos")
    private String cep;

    @Pattern(regexp = "^\\d{10,11}$", message = "Telefone deve ter 10 ou 11 dígitos")
    private String telefone;

    @NotNull(message = "Score é obrigatório")
    @Min(value = 0, message = "Score deve ser no mínimo 0")
    @Max(value = 1000, message = "Score deve ser no máximo 1000")
    private Integer score;

}
