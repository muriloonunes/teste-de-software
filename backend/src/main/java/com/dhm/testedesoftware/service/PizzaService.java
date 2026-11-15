package com.dhm.testedesoftware.service;

import com.dhm.testedesoftware.model.dto.PizzaRequestDTO;
import com.dhm.testedesoftware.model.dto.PizzaResponseDTO;
import com.dhm.testedesoftware.exception.EntidadeNaoEncontrada;
import com.dhm.testedesoftware.model.Pizza;
import com.dhm.testedesoftware.repository.PizzaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PizzaService {

    private final PizzaRepository pizzaRepository;

    public PizzaService(PizzaRepository pizzaRepository) {
        this.pizzaRepository = pizzaRepository;
    }

    public PizzaResponseDTO criar(PizzaRequestDTO dto) {
        Pizza pizza = new Pizza();
        pizza.setName(dto.name());
        pizza.setDescricao(dto.descricao());
        pizza.setCategoria(dto.categoria());
        pizza.setValorBase(dto.valorBase());
        return PizzaResponseDTO.fromEntity(pizzaRepository.save(pizza));
    }

    public PizzaResponseDTO atualizar(Long id, PizzaRequestDTO dto) {
        Pizza pizza = buscarPorId(id);
        pizza.setName(dto.name());
        pizza.setDescricao(dto.descricao());
        pizza.setCategoria(dto.categoria());
        pizza.setValorBase(dto.valorBase());
        return PizzaResponseDTO.fromEntity(pizzaRepository.save(pizza));
    }

    public List<PizzaResponseDTO> listarDTO() {
        return pizzaRepository.findAll().stream()
                .map(PizzaResponseDTO::fromEntity)
                .toList();
    }

    public Pizza buscarPorId(Long id) {
        return pizzaRepository.findById(id).orElseThrow(
                () -> new EntidadeNaoEncontrada("Pizza não encontrada com o id: " + id)
        );
    }

    public PizzaResponseDTO buscarDTOPorId(Long id) {
        return PizzaResponseDTO.fromEntity(buscarPorId(id));
    }

    public void excluir(Long id) {
        if (!pizzaRepository.existsById(id)) {
            throw new EntidadeNaoEncontrada("Pizza não encontrada com o id: " + id);
        }
        pizzaRepository.deleteById(id);
    }
}
