package com.dhm.testedesoftware.model.dto;

import com.dhm.testedesoftware.model.Pagamento;

public record PagamentoResponseDTO(
        Long id,
        String nome,
        Double taxa,
        String tipoPagamento,
        int parcelas
) {
    public static PagamentoResponseDTO fromEntity(Pagamento pagamento) {
        if (pagamento == null) {
            return null;
        }
        return new PagamentoResponseDTO(
                pagamento.getId(),
                pagamento.getNome(),
                pagamento.getTaxa(),
                pagamento.getTipoPagamento(),
                pagamento.getParcelas()
        );
    }
}
