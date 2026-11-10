package com.dhm.testedesoftware.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemAdicionalRequestDTO(
        @NotNull(message = "O id do adicional é obrigatório.")
        Long adicionalId,

        @NotNull(message = "A quantidade é obrigatória.")
        @Min(value = 1, message = "A quantidade deve ser maior que zero.")
        Integer quantidade
) {
}
