package com.dhm.testedesoftware.controller;

import com.dhm.testedesoftware.model.dto.LoginRequestDTO;
import com.dhm.testedesoftware.model.dto.UsuarioResponseDTO;
import com.dhm.testedesoftware.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    private final UsuarioService service;

    public AuthController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(service.autenticarDTO(request.email(), request.senha()));
    }
}
