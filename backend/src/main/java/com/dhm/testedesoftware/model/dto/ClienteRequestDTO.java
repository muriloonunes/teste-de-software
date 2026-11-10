package com.dhm.testedesoftware.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequestDTO(
        @NotBlank(message = "O nome do cliente é obrigatório.")
        String nome,

        @NotBlank(message = "O telefone do cliente é obrigatório.")
        String telefone,

        @Size(max = 255, message = "O endereço não pode ter mais de 255 caracteres")
        String endereco
) {
}
