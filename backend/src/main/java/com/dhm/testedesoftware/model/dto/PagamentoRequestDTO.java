package com.dhm.testedesoftware.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PagamentoRequestDTO(
        @NotBlank(message = "O nome do pagamento é obrigatório.")
        @Size(min = 1, max = 100, message = "O nome do pagamento deve ter entre 1 e 100 caracteres.")
        String nome,

        @NotNull(message = "A taxa é obrigatória.")
        @PositiveOrZero(message = "A taxa não pode ser negativa.")
        Double taxa,

        @NotBlank(message = "O tipo de pagamento é obrigatório.")
        @Size(min = 1, max = 100, message = "O tipo de pagamento deve ter entre 1 e 100 caracteres.")
        String tipoPagamento,

        @NotNull(message = "O número de parcelas é obrigatório.")
        @Min(value = 1, message = "O número mínimo de parcelas é 1.")
        @Max(value = 12, message = "O número máximo de parcelas é 12.")
        Integer parcelas
) {
}
