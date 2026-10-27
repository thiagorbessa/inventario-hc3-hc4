package com.inventario.controller;

import com.inventario.dto.EquipamentoDTO;
import com.inventario.service.EquipamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipamentos")
@Tag(name = "Equipamentos API", description = "Endpoints de gerenciamento e consulta de equipamentos de TI unificados")
public class EquipamentoController {

    @Autowired
    private EquipamentoService equipamentoService;

    @GetMapping
    @Operation(summary = "Listar equipamentos", description = "Retorna a lista completa de equipamentos de TI cadastrados no inventário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<List<EquipamentoDTO>> listar() {
        return ResponseEntity.ok(equipamentoService.listarTodos());
    }

    @GetMapping("/{patrimonio}")
    @Operation(summary = "Buscar por Patrimônio", description = "Busca os dados detalhados de um equipamento pelo código do Patrimônio do Micro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Equipamento não encontrado")
    })
    public ResponseEntity<EquipamentoDTO> buscarPorPatrimonio(@PathVariable String patrimonio) {
        return ResponseEntity.ok(equipamentoService.buscarPorPatrimonio(patrimonio));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    @Operation(summary = "Cadastrar/Salvar equipamento", description = "Cria um novo equipamento ou atualiza um registro existente. Requer perfil ADMIN ou MASTER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Equipamento criado/atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<EquipamentoDTO> salvar(@RequestBody EquipamentoDTO dto) {
        EquipamentoDTO salvo = equipamentoService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @DeleteMapping("/{patrimonio}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    @Operation(summary = "Excluir equipamento", description = "Remove permanentemente o equipamento da base de dados pelo Patrimônio. Requer perfil ADMIN ou MASTER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Equipamento excluído com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Void> deletar(@PathVariable String patrimonio) {
        equipamentoService.deletar(patrimonio);
        return ResponseEntity.noContent().build();
    }
}