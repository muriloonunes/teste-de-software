package com.dhm.testedesoftware.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 18/09/2026
 * @brief Enum Tamanho
 */
@Getter
@AllArgsConstructor
public enum TamanhoPizza {
    PEQUENA("Pequena", 4, 1.0),
    MEDIA("Média", 6, 1.5),
    GRANDE("Grande", 8, 1.8);

    private final String tamanho;
    private final int qtdFatias;
    private final double multiplicadorTamanho;
}
