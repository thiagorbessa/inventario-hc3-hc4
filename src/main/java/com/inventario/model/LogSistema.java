package com.inventario.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs_sistema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuario;
    private String acao; // Ex: CRIAR, EDITAR, DELETAR, IMPORTAR

    @Column(length = 1000)
    private String detalhe;

    private LocalDateTime dataHora;
}