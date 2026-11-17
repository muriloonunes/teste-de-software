package com.dhm.testedesoftware.service;

import com.dhm.testedesoftware.exception.CredenciaisInvalidasException;
import com.dhm.testedesoftware.exception.EmailJaCadastradoException;
import com.dhm.testedesoftware.exception.EntidadeNaoEncontrada;
import com.dhm.testedesoftware.model.Usuario;
import com.dhm.testedesoftware.model.dto.UsuarioRequestDTO;
import com.dhm.testedesoftware.model.dto.UsuarioResponseDTO;
import com.dhm.testedesoftware.model.dto.UsuarioUpdateDTO;
import com.dhm.testedesoftware.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (dto.senha() == null || dto.senha().trim().length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
        }
        if (repository.existsByEmailIgnoreCase(dto.email())) {
            throw new EmailJaCadastradoException("Já existe um usuário com este e-mail.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome().trim());
        usuario.setEmail(dto.email().trim());
        usuario.setSenha(passwordEncoder.encode(dto.senha().trim()));
        usuario.setAtivo(dto.ativo() == null || dto.ativo());
        return UsuarioResponseDTO.fromEntity(repository.save(usuario));
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        return atualizar(id, new UsuarioUpdateDTO(dto.nome(), dto.email(), dto.senha(), dto.ativo()));
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = buscarPorId(id);
        if (!usuario.getEmail().equalsIgnoreCase(dto.email()) && repository.existsByEmailIgnoreCase(dto.email())) {
            throw new EmailJaCadastradoException("Já existe um usuário com este e-mail.");
        }
        usuario.setNome(dto.nome().trim());
        usuario.setEmail(dto.email().trim());
        if (dto.senha() != null && !dto.senha().isEmpty()) {
            if (dto.senha().trim().length() < 6) {
                throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
            }
            usuario.setSenha(passwordEncoder.encode(dto.senha().trim()));
        }
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
                .orElseThrow(() -> new CredenciaisInvalidasException("E-mail ou senha inválidos."));
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new CredenciaisInvalidasException("E-mail ou senha inválidos.");
        }
        return usuario;
    }

    public UsuarioResponseDTO autenticarDTO(String email, String senha) {
        return UsuarioResponseDTO.fromEntity(autenticar(email, senha));
    }
}
