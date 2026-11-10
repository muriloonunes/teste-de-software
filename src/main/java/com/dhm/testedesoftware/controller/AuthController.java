package com.dhm.testedesoftware.controller;

import com.dhm.testedesoftware.model.Usuario;
import com.dhm.testedesoftware.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    public ResponseEntity<Usuario> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.autenticar(request.email(), request.senha()));
    }

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String senha
    ) {
    }
}
