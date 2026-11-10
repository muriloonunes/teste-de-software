package com.dhm.testedesoftware.service;

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

    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElseThrow(
                () -> new EntidadeNaoEncontrada("Cliente não encontrado com o id: " + id)
        );
    }

    public void excluir(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntidadeNaoEncontrada("Cliente não encontrado com o id: " + id);
        }
        clienteRepository.deleteById(id);
    }
}
