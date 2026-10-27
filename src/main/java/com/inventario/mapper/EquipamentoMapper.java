package com.inventario.mapper;

import com.inventario.dto.EquipamentoDTO;
import com.inventario.model.Equipamento;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EquipamentoMapper {

    public EquipamentoDTO toDTO(Equipamento entity) {
        if (entity == null) return null;

        return EquipamentoDTO.builder()
                .patrimonioMicro(entity.getPatrimonioMicro())
                .unidade(entity.getUnidade())
                .netname(entity.getNetname())
                .expressService(entity.getExpressService())
                .serviceTag(entity.getServiceTag())
                .modeloMicro(entity.getModeloMicro())
                .patrimonioMonitor(entity.getPatrimonioMonitor())
                .serviceTagMonitor(entity.getServiceTagMonitor())
                .serieMonitor(entity.getSerieMonitor())
                .macAddress(entity.getMacAddress())
                .local(entity.getLocal())
                .andar(entity.getAndar())
                .ramal(entity.getRamal())
                .ipMicro(entity.getIpMicro())
                .ipSwitch(entity.getIpSwitch())
                .portaSwitch(entity.getPortaSwitch())
                .rack(entity.getRack())
                .patchPanel(entity.getPatchPanel())
                .obs(entity.getObs())
                .build();
    }

    public Equipamento toEntity(EquipamentoDTO dto) {
        if (dto == null) return null;

        return Equipamento.builder()
                .patrimonioMicro(dto.getPatrimonioMicro() != null ? dto.getPatrimonioMicro().trim() : null)
                .unidade(dto.getUnidade())
                .netname(dto.getNetname() != null ? dto.getNetname().trim().toUpperCase() : null)
                .expressService(dto.getExpressService())
                .serviceTag(dto.getServiceTag())
                .modeloMicro(dto.getModeloMicro())
                .patrimonioMonitor(dto.getPatrimonioMonitor())
                .serviceTagMonitor(dto.getServiceTagMonitor())
                .serieMonitor(dto.getSerieMonitor())
                .macAddress(dto.getMacAddress() != null ? dto.getMacAddress().trim().toUpperCase() : null)
                .local(dto.getLocal())
                .andar(dto.getAndar())
                .ramal(dto.getRamal())
                .ipMicro(dto.getIpMicro() != null ? dto.getIpMicro().trim() : null)
                .ipSwitch(dto.getIpSwitch())
                .portaSwitch(dto.getPortaSwitch())
                .rack(dto.getRack())
                .patchPanel(dto.getPatchPanel())
                .obs(dto.getObs())
                .build();
    }

    public List<EquipamentoDTO> toDTOList(List<Equipamento> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}