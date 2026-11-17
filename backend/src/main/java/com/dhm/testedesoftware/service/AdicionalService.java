package com.dhm.testedesoftware.service;

import com.dhm.testedesoftware.model.dto.AdicionalRequestDTO;
import com.dhm.testedesoftware.model.dto.AdicionalResponseDTO;
import com.dhm.testedesoftware.exception.EntidadeNaoEncontrada;
import com.dhm.testedesoftware.model.Adicional;
import com.dhm.testedesoftware.repository.AdicionalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdicionalService {

    private final AdicionalRepository adicionalRepository;

    public AdicionalService(AdicionalRepository adicionalRepository) {
        this.adicionalRepository = adicionalRepository;
    }

    public AdicionalResponseDTO criar(AdicionalRequestDTO dto) {
        Adicional adicional = new Adicional();
        adicional.setNome(dto.nome());
        adicional.setDescricao(dto.descricao());
        adicional.setCategoria(dto.categoria());
        adicional.setPreco(dto.preco());
        return AdicionalResponseDTO.fromEntity(adicionalRepository.save(adicional));
    }

    public AdicionalResponseDTO atualizar(Long id, AdicionalRequestDTO dto) {
        Adicional adicional = buscarPorId(id);
        adicional.setNome(dto.nome());
        adicional.setDescricao(dto.descricao());
        adicional.setCategoria(dto.categoria());
        adicional.setPreco(dto.preco());
        return AdicionalResponseDTO.fromEntity(adicionalRepository.save(adicional));
    }

    public List<AdicionalResponseDTO> listarDTO() {
        return adicionalRepository.findAll().stream()
                .map(AdicionalResponseDTO::fromEntity)
                .toList();
    }

    public Adicional buscarPorId(Long id) {
        return adicionalRepository.findById(id).orElseThrow(
                () -> new EntidadeNaoEncontrada("Adicional não encontrado com id: " + id)
        );
    }

    public AdicionalResponseDTO buscarDTOPorId(Long id) {
        return AdicionalResponseDTO.fromEntity(buscarPorId(id));
    }

    public void excluir(Long id) {
        if (!adicionalRepository.existsById(id)) {
            throw new EntidadeNaoEncontrada("Adicional não encontrado com id: " + id);
        }
        adicionalRepository.deleteById(id);
    }
}
