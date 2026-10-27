package com.inventario.configuration;

import com.inventario.model.Role;
import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DadosIniciaisConfig {

    @Bean
    public CommandLineRunner carregarDadosIniciais(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!usuarioRepository.existsByUsername("admin")) {
                Usuario master = Usuario.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .nome("Administrador Master")
                        .role(Role.ROLE_MASTER)
                        .build();

                usuarioRepository.save(master);
                System.out.println("Utilizador MASTER criado com sucesso: admin / admin123");
            }
        };
    }
}