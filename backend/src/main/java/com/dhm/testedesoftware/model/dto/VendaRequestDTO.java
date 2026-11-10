package com.dhm.testedesoftware.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record VendaRequestDTO(
        Long clienteId,

        Long usuarioId,

        Long pagamentoId,

        @NotEmpty(message = "A venda deve conter pelo menos um item de pedido.")
        @Valid
        List<ItemPedidoRequestDTO> pedidos,

        @Valid
        List<ItemAdicionalRequestDTO> adicionais
) {
}
