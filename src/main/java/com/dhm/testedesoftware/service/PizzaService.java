package com.dhm.testedesoftware.service;

import com.dhm.testedesoftware.model.Pizza;
import com.dhm.testedesoftware.repository.PizzaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<Pizza> buscarPorId(Long id) {
        return pizzaRepository.findById(id);
    }

    public void excluir(Long id) {
        pizzaRepository.deleteById(id);
    }
}
