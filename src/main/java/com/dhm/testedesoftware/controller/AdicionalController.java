package com.dhm.testedesoftware.controller;

import com.dhm.testedesoftware.model.dto.AdicionalRequestDTO;
import com.dhm.testedesoftware.model.dto.AdicionalResponseDTO;
import com.dhm.testedesoftware.service.AdicionalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adicionais")
@CrossOrigin
public class AdicionalController {
    private final AdicionalService service;

    public AdicionalController(AdicionalService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AdicionalResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarDTO());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdicionalResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarDTOPorId(id));
    }

    @PostMapping
    public ResponseEntity<AdicionalResponseDTO> criar(@Valid @RequestBody AdicionalRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdicionalResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AdicionalRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
