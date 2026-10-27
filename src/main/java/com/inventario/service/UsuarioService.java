package com.inventario.service;

import com.inventario.dto.UsuarioDTO;
import com.inventario.exception.EntidadeNaoEncontradaException;
import com.inventario.exception.RegraNegocioException;
import com.inventario.mapper.UsuarioMapper;
import com.inventario.model.Role;
import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioMapper mapper;

    // --- LISTAGEM FILTRADA CONFORME QUEM ESTÁ LOGADO ---
    public List<UsuarioDTO> listarPorUsuarioLogado(Usuario usuarioLogado) {
        List<Usuario> todos = usuarioRepository.findAll();

        if (usuarioLogado.getRole() == Role.ROLE_MASTER) {
            // MASTER visualiza todas as contas (MASTER, ADMIN, VISUALIZADOR)
            return mapper.toDTOList(todos);
        } else {
            // ADMIN visualiza apenas contas que NÃO SEJAM MASTER (ou seja, ADMIN e VISUALIZADOR)
            List<Usuario> filtrados = todos.stream()
                    .filter(u -> u.getRole() != Role.ROLE_MASTER)
                    .collect(Collectors.toList());
            return mapper.toDTOList(filtrados);
        }
    }

    public List<UsuarioDTO> listarTodos() {
        return mapper.toDTOList(usuarioRepository.findAll());
    }

    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = buscarEntidadePorId(id);
        return mapper.toDTO(usuario);
    }

    public void salvar(UsuarioDTO dto, Usuario usuarioLogado) {
        if (dto.getId() != null) {
            // --- EDIÇÃO DE USUÁRIO ---
            Usuario usuarioExistente = buscarEntidadePorId(dto.getId());

            validarPermissoesEdicao(usuarioExistente, dto, usuarioLogado);

            // Verifica se o username mudou e se já pertence a outro usuário
            if (!usuarioExistente.getUsername().equals(dto.getUsername()) &&
                    usuarioRepository.findByUsername(dto.getUsername()).isPresent()) {
                throw new RegraNegocioException("Este nome de usuário já está em uso.");
            }

            usuarioExistente.setNome(dto.getNome());
            usuarioExistente.setUsername(dto.getUsername());

            // Apenas MASTER pode alterar Perfis de Acesso
            if (usuarioLogado.getRole() == Role.ROLE_MASTER) {
                usuarioExistente.setRole(dto.getRole());
            }

            // Atualiza a senha apenas se informada no formulário
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                usuarioExistente.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            usuarioRepository.save(usuarioExistente);

        } else {
            // --- CRIAÇÃO DE NOVO USUÁRIO ---
            if (usuarioLogado.getRole() == Role.ROLE_ADMIN) {
                // ADMIN força sempre a criação com perfil VISUALIZADOR
                dto.setRole(Role.ROLE_VISUALIZADOR);
            }

            if (usuarioRepository.findByUsername(dto.getUsername()).isPresent()) {
                throw new RegraNegocioException("Este nome de usuário já está em uso.");
            }

            Usuario novoUsuario = mapper.toEntity(dto);
            novoUsuario.setPassword(passwordEncoder.encode(dto.getPassword()));
            usuarioRepository.save(novoUsuario);
        }
    }

    public void resetarSenha(Long idUsuario, String novaSenha, Usuario usuarioLogado) {
        Usuario usuarioAlvo = buscarEntidadePorId(idUsuario);

        if (usuarioLogado.getRole() == Role.ROLE_ADMIN) {
            boolean eOProprioUsuario = usuarioAlvo.getId().equals(usuarioLogado.getId());

            if (!eOProprioUsuario && usuarioAlvo.getRole() != Role.ROLE_VISUALIZADOR) {
                throw new AccessDeniedException("Administradores só podem resetar a senha de si mesmos ou de usuários com perfil VISUALIZADOR.");
            }
        }

        if (novaSenha == null || novaSenha.trim().isEmpty()) {
            throw new RegraNegocioException("A nova senha não pode ser vazia.");
        }

        usuarioAlvo.setPassword(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuarioAlvo);
    }

    public void deletar(Long idParaDeletar, Usuario usuarioLogado) {
        Usuario usuarioAlvo = buscarEntidadePorId(idParaDeletar);

        if (usuarioLogado.getRole() == Role.ROLE_ADMIN) {
            boolean eOProprioUsuario = usuarioAlvo.getId().equals(usuarioLogado.getId());

            if (eOProprioUsuario) {
                throw new RegraNegocioException("Você não pode excluir a sua própria conta.");
            }

            if (usuarioAlvo.getRole() != Role.ROLE_VISUALIZADOR) {
                throw new AccessDeniedException("Administradores não têm permissão para excluir outros Administradores ou usuários Master.");
            }
        }

        usuarioRepository.delete(usuarioAlvo);
    }

    // --- MÉTODOS AUXILIARES DE VALIDAÇÃO ---

    private Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado com ID: " + id));
    }

    private void validarPermissoesEdicao(Usuario usuarioExistente, UsuarioDTO dadosForm, Usuario usuarioLogado) {
        if (usuarioLogado.getRole() == Role.ROLE_ADMIN) {
            boolean eOProprioUsuario = usuarioExistente.getId().equals(usuarioLogado.getId());

            if (!eOProprioUsuario && usuarioExistente.getRole() != Role.ROLE_VISUALIZADOR) {
                throw new AccessDeniedException("Administradores não têm permissão para alterar outros Administradores ou usuários Master.");
            }

            if (eOProprioUsuario && dadosForm.getRole() != usuarioExistente.getRole()) {
                throw new RegraNegocioException("Você não pode alterar o seu próprio perfil de acesso.");
            }

            if (!eOProprioUsuario && dadosForm.getRole() != Role.ROLE_VISUALIZADOR) {
                throw new AccessDeniedException("Administradores só podem atribuir o perfil VISUALIZADOR a outros usuários.");
            }
        }
    }
}