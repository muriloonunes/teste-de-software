package com.dhm.testedesoftware.model.dto;

import com.dhm.testedesoftware.model.TamanhoPizza;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemPedidoRequestDTO(
        @NotNull(message = "O id da pizza é obrigatório.")
        Long pizzaId,

        @NotNull(message = "O tamanho da pizza é obrigatório.")
        TamanhoPizza tamanho,

        @NotNull(message = "A quantidade é obrigatória.")
        @Min(value = 1, message = "A quantidade deve ser maior que zero.")
        Integer quantidade
) {
}
