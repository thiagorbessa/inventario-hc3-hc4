package com.inventario.mapper;

import com.inventario.dto.UsuarioDTO;
import com.inventario.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    public UsuarioDTO toDTO(Usuario entity) {
        if (entity == null) return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setUsername(entity.getUsername());
        dto.setRole(entity.getRole());
        // Não mapeamos o password para o DTO de listagem por segurança
        return dto;
    }

    public Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) return null;
        Usuario entity = new Usuario();
        entity.setId(dto.getId());
        entity.setNome(dto.getNome());
        entity.setUsername(dto.getUsername());
        entity.setRole(dto.getRole());
        entity.setPassword(dto.getPassword());
        return entity;
    }

    public List<UsuarioDTO> toDTOList(List<Usuario> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}