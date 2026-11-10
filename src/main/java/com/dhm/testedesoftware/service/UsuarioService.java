package com.dhm.testedesoftware.service;

import com.dhm.testedesoftware.model.dto.UsuarioRequestDTO;
import com.dhm.testedesoftware.model.dto.UsuarioResponseDTO;
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

    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (repository.existsByEmailIgnoreCase(dto.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com este e-mail.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setAtivo(dto.ativo() == null || dto.ativo());
        return UsuarioResponseDTO.fromEntity(repository.save(usuario));
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = buscarPorId(id);
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        if (dto.ativo() != null) {
            usuario.setAtivo(dto.ativo());
        }
        return UsuarioResponseDTO.fromEntity(repository.save(usuario));
    }

    public List<UsuarioResponseDTO> listarDTO() {
        return repository.findAll().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .toList();
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntidadeNaoEncontrada("Usuário não encontrado com o id: " + id)
        );
    }

    public UsuarioResponseDTO buscarDTOPorId(Long id) {
        return UsuarioResponseDTO.fromEntity(buscarPorId(id));
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

    public UsuarioResponseDTO autenticarDTO(String email, String senha) {
        return UsuarioResponseDTO.fromEntity(autenticar(email, senha));
    }
}
