package com.dhm.testedesoftware.controller;

import com.dhm.testedesoftware.model.dto.PizzaRequestDTO;
import com.dhm.testedesoftware.model.dto.PizzaResponseDTO;
import com.dhm.testedesoftware.service.PizzaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pizzas")
@CrossOrigin
public class PizzaController {
    private final PizzaService service;

    public PizzaController(PizzaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PizzaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PizzaResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarDTOPorId(id));
    }

    @PostMapping
    public ResponseEntity<PizzaResponseDTO> criar(@Valid @RequestBody PizzaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PizzaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PizzaRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
