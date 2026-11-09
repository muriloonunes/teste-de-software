package com.dhm.testedesoftware.controller;

import com.dhm.testedesoftware.model.Pizza;
import com.dhm.testedesoftware.service.PizzaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pizzas")
@CrossOrigin
public class PizzaController {
    private final PizzaService service;
    public PizzaController(PizzaService service) { this.service = service; }

    @GetMapping public List<Pizza> listar() { return service.listar(); }
    @GetMapping("/{id}") public ResponseEntity<Pizza> buscar(@PathVariable Long id) {
        return CrudController.orNotFound(service.buscarPorId(id));
    }
    @PostMapping public ResponseEntity<Pizza> criar(@Valid @RequestBody Pizza pizza) {
        return ResponseEntity.status(201).body(service.salvar(pizza));
    }
    @PutMapping("/{id}") public ResponseEntity<Pizza> atualizar(@PathVariable Long id, @Valid @RequestBody Pizza pizza) {
        CrudController.requireId(id); pizza.setId(id); return ResponseEntity.ok(service.salvar(pizza));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (service.buscarPorId(id).isEmpty()) return ResponseEntity.notFound().build();
        service.excluir(id); return ResponseEntity.noContent().build();
    }
}
