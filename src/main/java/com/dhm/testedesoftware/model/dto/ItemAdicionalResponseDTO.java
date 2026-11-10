package com.dhm.testedesoftware.model.dto;

import com.dhm.testedesoftware.model.ItemAdicional;

import java.math.BigDecimal;

public record ItemAdicionalResponseDTO(
        Long id,
        AdicionalResponseDTO adicional,
        Integer quantidade,
        BigDecimal precoUnitario
) {
    public static ItemAdicionalResponseDTO fromEntity(ItemAdicional item) {
        if (item == null) {
            return null;
        }
        return new ItemAdicionalResponseDTO(
                item.getId(),
                AdicionalResponseDTO.fromEntity(item.getAdicional()),
                item.getQuantidade(),
                item.getPrecoUnitario()
        );
    }
}
