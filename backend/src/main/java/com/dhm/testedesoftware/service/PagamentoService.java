package com.dhm.testedesoftware.service;

import com.dhm.testedesoftware.exception.EntidadeNaoEncontrada;
import com.dhm.testedesoftware.model.Pagamento;
import com.dhm.testedesoftware.model.dto.PagamentoRequestDTO;
import com.dhm.testedesoftware.model.dto.PagamentoResponseDTO;
import com.dhm.testedesoftware.repository.PagamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }

    public PagamentoResponseDTO criar(PagamentoRequestDTO dto) {
        Pagamento pagamento = new Pagamento();
        pagamento.setNome(dto.nome());
        pagamento.setTaxa(dto.taxa());
        pagamento.setTipoPagamento(dto.tipoPagamento());
        pagamento.setParcelas(dto.parcelas());
        return PagamentoResponseDTO.fromEntity(pagamentoRepository.save(pagamento));
    }

    public PagamentoResponseDTO atualizar(Long id, PagamentoRequestDTO dto) {
        Pagamento pagamento = buscarPorId(id);
        pagamento.setNome(dto.nome());
        pagamento.setTaxa(dto.taxa());
        pagamento.setTipoPagamento(dto.tipoPagamento());
        pagamento.setParcelas(dto.parcelas());
        return PagamentoResponseDTO.fromEntity(pagamentoRepository.save(pagamento));
    }

    public List<PagamentoResponseDTO> listarDTO() {
        return pagamentoRepository.findAll().stream()
                .map(PagamentoResponseDTO::fromEntity)
                .toList();
    }

    public Pagamento buscarPorId(Long id) {
        return pagamentoRepository.findById(id).orElseThrow(
                () -> new EntidadeNaoEncontrada("Pagamento não encontrado com o id: " + id)
        );
    }

    public PagamentoResponseDTO buscarDTOPorId(Long id) {
        return PagamentoResponseDTO.fromEntity(buscarPorId(id));
    }

    public void excluir(Long id) {
        if (!pagamentoRepository.existsById(id)) {
            throw new EntidadeNaoEncontrada("Pagamento não encontrado com o id: " + id);
        }
        pagamentoRepository.deleteById(id);
    }
}
