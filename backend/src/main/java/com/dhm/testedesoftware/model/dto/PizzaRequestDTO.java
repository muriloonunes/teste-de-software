package com.dhm.testedesoftware.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PizzaRequestDTO(
        @NotBlank(message = "O nome da pizza é obrigatório.")
        @Size(max = 200, message = "O nome da pizza não pode ter mais de 200 caracteres")
        String name,

        @NotBlank(message = "A descrição da pizza é obrigatória.")
        @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres")
        String descricao,

        @NotNull(message = "O preço da pizza é obrigatório.")
        @Positive(message = "O preço da pizza deve ser maior que zero.")
        BigDecimal valorBase
) {
}
