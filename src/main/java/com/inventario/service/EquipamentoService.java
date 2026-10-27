package com.inventario.service;

import com.inventario.dto.EquipamentoDTO;
import com.inventario.exception.EntidadeNaoEncontradaException;
import com.inventario.exception.RegraNegocioException;
import com.inventario.mapper.EquipamentoMapper;
import com.inventario.model.Equipamento;
import com.inventario.repository.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipamentoService {

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private EquipamentoMapper mapper;

    public List<EquipamentoDTO> listarTodos() {
        return mapper.toDTOList(equipamentoRepository.findAll());
    }

    public EquipamentoDTO buscarPorPatrimonio(String patrimonio) {
        Equipamento equip = equipamentoRepository.findById(patrimonio)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Equipamento não encontrado com o Patrimônio: " + patrimonio));
        return mapper.toDTO(equip);
    }

    // Método padrão (usado pela API REST / EquipamentoController)
    public EquipamentoDTO salvar(EquipamentoDTO dto) {
        return salvar(dto, null);
    }

    // Método com suporte a alteração do Patrimônio Antigo (usado pelo WebController / Formulário)
    public EquipamentoDTO salvar(EquipamentoDTO dto, String patrimonioAntigo) {
        if (dto.getPatrimonioMicro() == null || dto.getPatrimonioMicro().trim().isEmpty()) {
            throw new RegraNegocioException("O número de Patrimônio do Micro é obrigatório.");
        }

        // Se o Patrimônio antigo foi alterado na tela de edição
        if (patrimonioAntigo != null && !patrimonioAntigo.trim().isEmpty()
                && !patrimonioAntigo.equals(dto.getPatrimonioMicro())) {

            // Remove o registro antigo com a chave primária antiga se ele existir no banco
            if (equipamentoRepository.existsById(patrimonioAntigo)) {
                equipamentoRepository.deleteById(patrimonioAntigo);
            }
        }

        Equipamento equipamento = mapper.toEntity(dto);
        Equipamento salvo = equipamentoRepository.save(equipamento);
        return mapper.toDTO(salvo);
    }

    public void deletar(String patrimonio) {
        if (!equipamentoRepository.existsById(patrimonio)) {
            throw new EntidadeNaoEncontradaException("Equipamento não encontrado para exclusão com o Patrimônio: " + patrimonio);
        }
        equipamentoRepository.deleteById(patrimonio);
    }
}