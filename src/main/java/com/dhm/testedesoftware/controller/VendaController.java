package com.dhm.testedesoftware.controller;

import com.dhm.testedesoftware.model.Venda;
import com.dhm.testedesoftware.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@CrossOrigin
public class VendaController {
    private final VendaService service;
    public VendaController(VendaService service) { this.service = service; }
    @GetMapping public List<Venda> listar() { return service.listar(); }
    @GetMapping("/historico/{usuarioId}") public List<Venda> historico(@PathVariable Long usuarioId) {
        return service.historico(usuarioId);
    }
    @GetMapping("/{id}") public ResponseEntity<Venda> buscar(@PathVariable Long id) {
        return CrudController.orNotFound(service.buscarPorId(id));
    }
    @PostMapping public ResponseEntity<Venda> criar(@Valid @RequestBody Venda venda) {
        return ResponseEntity.status(201).body(service.salvar(venda));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (service.buscarPorId(id).isEmpty()) return ResponseEntity.notFound().build();
        service.excluir(id); return ResponseEntity.noContent().build();
    }
}
