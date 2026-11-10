package com.dhm.testedesoftware.service;

import com.dhm.testedesoftware.exception.EntidadeNaoEncontrada;
import com.dhm.testedesoftware.model.Usuario;
import com.dhm.testedesoftware.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario salvar(Usuario usuario) {
        if (usuario.getId() == null && repository.existsByEmailIgnoreCase(usuario.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com este e-mail.");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return repository.save(usuario);
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntidadeNaoEncontrada("Usuário não encontrado com o id: " + id)
        );
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new EntidadeNaoEncontrada("Usuário não encontrado com o id: " + id);
        }
        repository.deleteById(id);
    }

    public Usuario autenticar(String email, String senha) {
        Usuario usuario = repository.findByEmailIgnoreCase(email)
                .filter(Usuario::isAtivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos."));
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
        }
        return usuario;
    }
}
