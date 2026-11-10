package com.dhm.testedesoftware.model.dto;

import com.dhm.testedesoftware.model.ItemPedido;
import com.dhm.testedesoftware.model.TamanhoPizza;

import java.math.BigDecimal;

public record ItemPedidoResponseDTO(
        Long id,
        PizzaResponseDTO pizza,
        TamanhoPizza tamanho,
        Integer quantidade,
        BigDecimal valorPedido
) {
    public static ItemPedidoResponseDTO fromEntity(ItemPedido item) {
        if (item == null) {
            return null;
        }
        return new ItemPedidoResponseDTO(
                item.getId(),
                PizzaResponseDTO.fromEntity(item.getPizza()),
                item.getTamanho(),
                item.getQuantidade(),
                item.getValorPedido()
        );
    }
}
