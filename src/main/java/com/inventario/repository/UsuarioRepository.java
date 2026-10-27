package com.inventario.repository;

import com.inventario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo nome de usuário (login)
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Verifica se já existe um usuário cadastrado com o username informado
     */
    boolean existsByUsername(String username);
}