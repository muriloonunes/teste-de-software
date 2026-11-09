package com.dhm.testedesoftware.controller;

import com.dhm.testedesoftware.model.Adicional;
import com.dhm.testedesoftware.service.AdicionalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/adicionais")
@CrossOrigin
public class AdicionalController {
    private final AdicionalService service;
    public AdicionalController(AdicionalService service) { this.service = service; }
    @GetMapping public List<Adicional> listar() { return service.listar(); }
    @GetMapping("/{id}") public ResponseEntity<Adicional> buscar(@PathVariable Long id) {
        return CrudController.orNotFound(service.buscarPorId(id));
    }
    @PostMapping public ResponseEntity<Adicional> criar(@Valid @RequestBody Adicional item) {
        return ResponseEntity.status(201).body(service.salvar(item));
    }
    @PutMapping("/{id}") public ResponseEntity<Adicional> atualizar(@PathVariable Long id, @Valid @RequestBody Adicional item) {
        item.setId(id); return ResponseEntity.ok(service.salvar(item));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (service.buscarPorId(id).isEmpty()) return ResponseEntity.notFound().build();
        service.excluir(id); return ResponseEntity.noContent().build();
    }
}
