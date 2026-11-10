package com.dhm.testedesoftware.model.dto;

import com.dhm.testedesoftware.model.Adicional;

import java.math.BigDecimal;

public record AdicionalResponseDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco
) {
    public static AdicionalResponseDTO fromEntity(Adicional adicional) {
        if (adicional == null) {
            return null;
        }
        return new AdicionalResponseDTO(
                adicional.getId(),
                adicional.getNome(),
                adicional.getDescricao(),
                adicional.getPreco()
        );
    }
}
