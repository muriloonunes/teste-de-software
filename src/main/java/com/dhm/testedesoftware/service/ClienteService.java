package com.dhm.testedesoftware.service;

import com.dhm.testedesoftware.model.dto.ClienteRequestDTO;
import com.dhm.testedesoftware.model.dto.ClienteResponseDTO;
import com.dhm.testedesoftware.exception.EntidadeNaoEncontrada;
import com.dhm.testedesoftware.model.Cliente;
import com.dhm.testedesoftware.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setTelefone(dto.telefone());
        cliente.setEndereco(dto.endereco());
        return ClienteResponseDTO.fromEntity(clienteRepository.save(cliente));
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = buscarPorId(id);
        cliente.setNome(dto.nome());
        cliente.setTelefone(dto.telefone());
        cliente.setEndereco(dto.endereco());
        return ClienteResponseDTO.fromEntity(clienteRepository.save(cliente));
    }

    public List<ClienteResponseDTO> listarDTO() {
        return clienteRepository.findAll().stream()
                .map(ClienteResponseDTO::fromEntity)
                .toList();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElseThrow(
                () -> new EntidadeNaoEncontrada("Cliente não encontrado com o id: " + id)
        );
    }

    public ClienteResponseDTO buscarDTOPorId(Long id) {
        return ClienteResponseDTO.fromEntity(buscarPorId(id));
    }

    public void excluir(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntidadeNaoEncontrada("Cliente não encontrado com o id: " + id);
        }
        clienteRepository.deleteById(id);
    }
}
