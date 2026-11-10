package com.dhm.testedesoftware.service;

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

    public Pizza salvar(Pizza pizza) {
        return pizzaRepository.save(pizza);
    }

    public List<Pizza> listar() {
        return pizzaRepository.findAll();
    }

    public Pizza buscarPorId(Long id) {
        return pizzaRepository.findById(id).orElseThrow(
                () -> new EntidadeNaoEncontrada("Pizza não encontrada com o id: " + id)
        );
    }

    public void excluir(Long id) {
        if (!pizzaRepository.existsById(id)) {
            throw new EntidadeNaoEncontrada("Pizza não encontrada com o id: " + id);
        }
        pizzaRepository.deleteById(id);
    }
}
