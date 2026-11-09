package com.dhm.testedesoftware.controller;

import com.dhm.testedesoftware.model.Usuario;
import com.dhm.testedesoftware.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin
public class UsuarioController {
    private final UsuarioService service;
    public UsuarioController(UsuarioService service) { this.service = service; }
    @GetMapping public List<Usuario> listar() { return service.listar(); }
    @GetMapping("/{id}") public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        return CrudController.orNotFound(service.buscarPorId(id));
    }
    @PostMapping public ResponseEntity<Usuario> criar(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.status(201).body(service.salvar(usuario));
    }
    @PutMapping("/{id}") public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        usuario.setId(id); return ResponseEntity.ok(service.salvar(usuario));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (service.buscarPorId(id).isEmpty()) return ResponseEntity.notFound().build();
        service.excluir(id); return ResponseEntity.noContent().build();
    }
}
