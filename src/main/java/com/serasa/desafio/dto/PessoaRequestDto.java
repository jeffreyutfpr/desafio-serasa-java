package com.serasa.desafio.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaRequestDto {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Min(value = 0, message = "Idade deve ser maior ou igual a 0")
    @Max(value = 120, message = "Idade deve ser menor ou igual a 120")
    private Integer idade;

    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve ter 8 dígitos, com ou sem hífen")
    private String cep;

    private String telefone;

    @Min(value = 0, message = "Score deve ser maior ou igual a 0")
    @Max(value = 1000, message = "Score deve ser menor ou igual a 1000")
    private Integer score;
}
