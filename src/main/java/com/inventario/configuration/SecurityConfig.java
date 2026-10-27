package com.inventario.configuration;

import com.inventario.service.AutenticacaoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AutenticacaoService autenticacaoService;

    public SecurityConfig(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(autenticacaoService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Libera recursos estáticos (CSS, JS, Imagens)
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // Libera a documentação do Swagger UI / OpenAPI 3
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Libera a consola do H2 temporariamente
                        .requestMatchers("/h2-console/**").permitAll()

                        // Qualquer outra requisição precisa estar autenticada
                        .anyRequest().authenticated()
                )
                // Permite frames para o H2 Console funcionar
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))

                // Configuração padrão de Login do Spring Security
                .formLogin(form -> form
                        .defaultSuccessUrl("/", true) // Redireciona para o Dashboard (/) após login
                        .permitAll() // Permite acesso irrestrito à página de login
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout") // Redireciona para a página de login após o logout
                        .permitAll()
                );

        // Adiciona o provedor de autenticação com o nosso serviço e o BCrypt
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }
}