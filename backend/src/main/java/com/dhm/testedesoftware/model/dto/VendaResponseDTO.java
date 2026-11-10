package com.dhm.testedesoftware.model.dto;

import com.dhm.testedesoftware.model.Venda;

import java.math.BigDecimal;
import java.util.List;

public record VendaResponseDTO(
        Long id,
        ClienteResponseDTO cliente,
        UsuarioResponseDTO usuario,
        PagamentoResponseDTO pagamento,
        List<ItemPedidoResponseDTO> pedidos,
        List<ItemAdicionalResponseDTO> adicionais,
        BigDecimal valor
) {
    public static VendaResponseDTO fromEntity(Venda venda) {
        if (venda == null) {
            return null;
        }
        List<ItemPedidoResponseDTO> pedidosDto = venda.getPedidos() == null
                ? List.of()
                : venda.getPedidos().stream().map(ItemPedidoResponseDTO::fromEntity).toList();

        List<ItemAdicionalResponseDTO> adicionaisDto = venda.getAdicionais() == null
                ? List.of()
                : venda.getAdicionais().stream().map(ItemAdicionalResponseDTO::fromEntity).toList();

        return new VendaResponseDTO(
                venda.getId(),
                ClienteResponseDTO.fromEntity(venda.getCliente()),
                UsuarioResponseDTO.fromEntity(venda.getUsuario()),
                PagamentoResponseDTO.fromEntity(venda.getPagamento()),
                pedidosDto,
                adicionaisDto,
                venda.getValor()
        );
    }
}
