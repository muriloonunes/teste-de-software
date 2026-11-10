package com.dhm.testedesoftware.model.dto;

import com.dhm.testedesoftware.model.Usuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        boolean ativo
) {
    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.isAtivo()
        );
    }
}
