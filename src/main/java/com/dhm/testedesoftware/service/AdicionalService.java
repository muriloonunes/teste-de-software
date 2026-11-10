package com.dhm.testedesoftware.service;

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

    public Adicional salvar(Adicional adicional) {
        return adicionalRepository.save(adicional);
    }

    public List<Adicional> listar() {
        return adicionalRepository.findAll();
    }

    public Adicional buscarPorId(Long id) {
        return adicionalRepository.findById(id).orElseThrow(
                () -> new EntidadeNaoEncontrada("Adicional não encontrado com id: " + id)
        );
    }

    public void excluir(Long id) {
        if (!adicionalRepository.existsById(id)) {
            throw new EntidadeNaoEncontrada("Adicional não encontrado com id: " + id);
        }
        adicionalRepository.deleteById(id);
    }
}
