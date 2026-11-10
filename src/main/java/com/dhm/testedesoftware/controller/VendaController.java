package com.dhm.testedesoftware.controller;

import com.dhm.testedesoftware.model.Venda;
import com.dhm.testedesoftware.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@CrossOrigin
public class VendaController {
    private final VendaService service;

    public VendaController(VendaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Venda>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/historico/{usuarioId}")
    public ResponseEntity<List<Venda>> historico(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.historico(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Venda> criar(@Valid @RequestBody Venda venda) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(venda));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
