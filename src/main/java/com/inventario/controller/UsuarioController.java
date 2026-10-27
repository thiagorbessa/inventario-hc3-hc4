package com.inventario.controller;

import com.inventario.dto.UsuarioDTO;
import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import com.inventario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public ResponseEntity<Void> salvar(@RequestBody UsuarioDTO usuarioDTO,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        Usuario logado = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        usuarioService.salvar(usuarioDTO, logado);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public ResponseEntity<Void> deletar(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        Usuario logado = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        usuarioService.deletar(id, logado);
        return ResponseEntity.noContent().build();
    }
}