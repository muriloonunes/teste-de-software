package com.dhm.testedesoftware.controller;

import com.dhm.testedesoftware.model.dto.VendaRequestDTO;
import com.dhm.testedesoftware.model.dto.VendaResponseDTO;
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
    public ResponseEntity<List<VendaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarDTO());
    }

    @GetMapping("/historico/{usuarioId}")
    public ResponseEntity<List<VendaResponseDTO>> historico(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.historicoDTO(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarDTOPorId(id));
    }

    @PostMapping
    public ResponseEntity<VendaResponseDTO> criar(@Valid @RequestBody VendaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
