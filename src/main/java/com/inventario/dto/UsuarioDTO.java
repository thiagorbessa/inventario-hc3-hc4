package com.inventario.dto;

import com.inventario.model.Role;
import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String username;
    private Role role;
    private String password; // Usado apenas no form de cadastro/edição, vazio na listagem
}