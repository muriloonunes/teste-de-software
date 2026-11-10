package com.dhm.testedesoftware.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AdicionalRequestDTO(
        @NotBlank(message = "O nome do adicional é obrigatório.")
        @Size(min = 1, max = 100, message = "O nome do adicional deve ter entre 1 e 100 caracteres.")
        String nome,

        @Size(max = 255, message = "A descrição não pode ter mais de 255 caracteres.")
        String descricao,

        @NotNull(message = "O preço do adicional é obrigatório.")
        @Positive(message = "O preço do adicional deve ser maior que zero.")
        BigDecimal preco
) {
}
