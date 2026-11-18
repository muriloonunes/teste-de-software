package com.dhm.testedesoftware.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateDTO(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 120, message = "O nome não pode ter mais de 120 caracteres.")
        String nome,

        @Email(message = "Informe um e-mail válido.")
        @NotBlank(message = "O e-mail é obrigatório.")
        @Size(max = 160, message = "O e-mail não pode ter mais de 160 caracteres.")
        String email,

        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
        String senha,

        Boolean ativo
) {
}
