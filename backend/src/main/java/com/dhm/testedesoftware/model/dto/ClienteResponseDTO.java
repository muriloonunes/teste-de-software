package com.dhm.testedesoftware.model.dto;

import com.dhm.testedesoftware.model.Cliente;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String telefone,
        String endereco
) {
    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEndereco()
        );
    }
}
