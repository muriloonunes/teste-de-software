package com.dhm.testedesoftware.service;

import com.dhm.testedesoftware.model.Adicional;
import com.dhm.testedesoftware.repository.AdicionalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<Adicional> buscarPorId(Long id) {
        return adicionalRepository.findById(id);
    }

    public void excluir(Long id) {
        adicionalRepository.deleteById(id);
    }
}
