package com.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipamentoDTO {

    private String patrimonioMicro;
    private String unidade;
    private String netname;
    private String expressService;
    private String serviceTag;
    private String modeloMicro;

    private String patrimonioMonitor;
    private String serviceTagMonitor;
    private String serieMonitor;

    private String macAddress;
    private String local;
    private String andar;
    private String ramal;
    private String ipMicro;
    private String ipSwitch;
    private String portaSwitch;
    private String rack;
    private String patchPanel;
    private String obs;
}