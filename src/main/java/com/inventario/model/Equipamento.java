package com.inventario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "equipamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipamento {

    @Id
    private String patrimonioMicro;   // Chave Primária

    private String unidade;
    private String netname;
    private String expressService;
    private String serviceTag;
    private String modeloMicro;

    private String patrimonioMonitor;
    private String serviceTagMonitor;
    private String serieMonitor;

    private String macAddress;
    private String local;             // Sala / Setor
    private String andar;             // Andar (Físico)
    private String ramal;
    private String ipMicro;
    private String ipSwitch;
    private String portaSwitch;
    private String rack;              // Rack de Rede
    private String patchPanel;

    @Column(length = 1000)
    private String obs;
}