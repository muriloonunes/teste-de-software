package com.dhm.testedesoftware.model.dto;

import com.dhm.testedesoftware.model.Pizza;

import java.math.BigDecimal;

public record PizzaResponseDTO(
        Long id,
        String name,
        String descricao,
        String categoria,
        BigDecimal valorBase
) {
    public static PizzaResponseDTO fromEntity(Pizza pizza) {
        if (pizza == null) {
            return null;
        }
        return new PizzaResponseDTO(
                pizza.getId(),
                pizza.getName(),
                pizza.getDescricao(),
                pizza.getCategoria(),
                pizza.getValorBase()
        );
    }
}
