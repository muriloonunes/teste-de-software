package com.dhm.testedesoftware.controller;

import com.dhm.testedesoftware.model.Cliente;
import com.dhm.testedesoftware.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service = service; }
    @GetMapping public List<Cliente> listar() { return service.listar(); }
    @GetMapping("/{id}") public ResponseEntity<Cliente> buscar(@PathVariable Long id) {
        return CrudController.orNotFound(service.buscarPorId(id));
    }
    @PostMapping public ResponseEntity<Cliente> criar(@Valid @RequestBody Cliente cliente) {
        return ResponseEntity.status(201).body(service.salvar(cliente));
    }
    @PutMapping("/{id}") public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        cliente.setId(id); return ResponseEntity.ok(service.salvar(cliente));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (service.buscarPorId(id).isEmpty()) return ResponseEntity.notFound().build();
        service.excluir(id); return ResponseEntity.noContent().build();
    }
}
