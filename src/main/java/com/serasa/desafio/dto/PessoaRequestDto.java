package com.serasa.desafio.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaRequestDto {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Idade é obrigatória")
    @Min(value = 0, message = "Idade deve ser maior ou igual a 0")
    @Max(value = 120, message = "Idade deve ser menor ou igual a 120")
    private Integer idade;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve ter exatamente 8 dígitos")
    private String cep;

    private String telefone;

    @NotNull(message = "Score é obrigatório")
    @Min(value = 0, message = "Score deve ser maior ou igual a 0")
    @Max(value = 1000, message = "Score deve ser menor ou igual a 1000")
    private Integer score;
}
