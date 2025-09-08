package com.serasa.desafio.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "scores")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer valor;

    @Transient
    public String getDescricao() {
        if (valor == null) return null;
        if (valor <= 200) return "Insuficiente";
        if (valor <= 500) return "Inaceitável";
        if (valor <= 700) return "Aceitável";
        return "Recomendável";
    }
}
