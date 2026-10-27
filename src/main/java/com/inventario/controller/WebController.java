package com.inventario.controller;

import com.inventario.dto.EquipamentoDTO;
import com.inventario.dto.UsuarioDTO;
import com.inventario.model.Role;
import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import com.inventario.service.EquipamentoService;
import com.inventario.service.LogService;
import com.inventario.service.PlanilhaService;
import com.inventario.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    private EquipamentoService equipamentoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlanilhaService planilhaService;

    @Autowired
    private LogService logService;

    // ==========================================
    // --- DASHBOARD E EQUIPAMENTOS ---
    // ==========================================

    @GetMapping("/")
    public String index(Model model) {
        List<EquipamentoDTO> lista = equipamentoService.listarTodos();

        long totalHc3 = lista.stream().filter(e -> "HC3".equalsIgnoreCase(e.getUnidade())).count();
        long totalHc4 = lista.stream().filter(e -> "HC4".equalsIgnoreCase(e.getUnidade())).count();

        // MÉTRICAS DO SERVIDOR (HEALTH CHECK & UPTIME)
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long uptimeMs = runtimeBean.getUptime();
        long uptimeMinutos = (uptimeMs / 1000) / 60;

        Runtime runtime = Runtime.getRuntime();
        long memUsadaMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long memTotalMb = runtime.maxMemory() / (1024 * 1024);

        model.addAttribute("equipamentos", lista);
        model.addAttribute("totalHc3", totalHc3);
        model.addAttribute("totalHc4", totalHc4);
        model.addAttribute("uptimeMinutos", uptimeMinutos);
        model.addAttribute("memUsadaMb", memUsadaMb);
        model.addAttribute("memTotalMb", memTotalMb);

        return "index";
    }

    @GetMapping("/equipamentos/novo")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public String novoEquipamento(Model model) {
        model.addAttribute("equipamento", new EquipamentoDTO());
        return "formulario";
    }

    @PostMapping("/equipamentos/salvar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public String salvarEquipamento(@ModelAttribute("equipamento") EquipamentoDTO dto,
                                    @RequestParam(value = "patrimonioAntigo", required = false) String patrimonioAntigo,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        equipamentoService.salvar(dto, patrimonioAntigo);

        String acao = (patrimonioAntigo != null && !patrimonioAntigo.isEmpty()) ? "EDITAR_EQUIPAMENTO" : "CRIAR_EQUIPAMENTO";
        logService.registrarLog(userDetails.getUsername(), acao,
                "Equipamento Pat: " + dto.getPatrimonioMicro() + " | Netname: " + dto.getNetname() + " | Local: " + dto.getLocal());

        return "redirect:/";
    }

    @GetMapping("/equipamentos/editar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public String editarEquipamento(@PathVariable String id, Model model) {
        EquipamentoDTO dto = equipamentoService.buscarPorPatrimonio(id);
        model.addAttribute("equipamento", dto);
        return "formulario";
    }

    @PostMapping("/equipamentos/deletar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public String deletarEquipamento(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
        equipamentoService.deletar(id);
        logService.registrarLog(userDetails.getUsername(), "EXCLUIR_EQUIPAMENTO", "Removido equipamento código: " + id);
        return "redirect:/";
    }

    // ==========================================
    // --- IMPORTAR E EXPORTAR EXCEL / TEXTO ---
    // ==========================================

    @PostMapping("/equipamentos/importar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public String importarPlanilha(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        try {
            planilhaService.importarDeExcel(file);
            logService.registrarLog(userDetails.getUsername(), "IMPORTAR_EXCEL", "Importou o arquivo: " + file.getOriginalFilename());
            redirectAttributes.addFlashAttribute("sucesso", "Planilha importada e mesclada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao importar planilha: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/equipamentos/exportar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public void exportarPlanilha(
            @RequestParam(value = "unidade", required = false) String unidade,
            @RequestParam(value = "netname", required = false) String netname,
            @RequestParam(value = "modelo", required = false) String modelo,
            @RequestParam(value = "local", required = false) String local,
            @RequestParam(value = "andar", required = false) String andar,
            @RequestParam(value = "geral", required = false) String geral,
            HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=inventario_hc3_hc4.xlsx");
            planilhaService.exportarParaExcelFiltrado(unidade, netname, modelo, local, andar, geral, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/equipamentos/importar-texto")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public String importarTextoColado(@RequestParam("textoColado") String textoColado,
                                      RedirectAttributes redirectAttributes,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        try {
            planilhaService.importarDeTextoColado(textoColado);
            logService.registrarLog(userDetails.getUsername(), "IMPORTAR_TEXTO", "Importou dados via Copiar/Colar Texto.");
            redirectAttributes.addFlashAttribute("sucesso", "Dados copiados processados e mesclados com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao processar texto colado: " + e.getMessage());
        }
        return "redirect:/";
    }

    // ==========================================
    // --- HISTÓRICO DE LOGS E AUDITORIA (MASTER) ---
    // ==========================================

    @GetMapping("/logs")
    @PreAuthorize("hasRole('MASTER')")
    public String paginaLogs(Model model) {
        model.addAttribute("logs", logService.listarTodos());
        return "logs";
    }

    // ==========================================
    // --- GESTÃO DE USUÁRIOS (THYMELEAF) ---
    // ==========================================

    @GetMapping("/usuarios")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public String listarUsuarios(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario logado = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        // Passa os usuários filtrados dependendo da Role de quem está logado (ADMIN só vê VISUALIZADOR)
        model.addAttribute("usuarios", usuarioService.listarPorUsuarioLogado(logado));
        model.addAttribute("usuarioLogado", logado);
        return "usuarios/listar-usuarios";
    }

    @GetMapping("/usuarios/novo")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public String novoUsuario(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario logado = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        model.addAttribute("usuario", new UsuarioDTO());
        model.addAttribute("usuarioLogado", logado);
        model.addAttribute("roles", Role.values());
        return "usuarios/formulario";
    }

    @GetMapping("/usuarios/editar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public String editarUsuario(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario logado = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        UsuarioDTO dto = usuarioService.buscarPorId(id);

        model.addAttribute("usuario", dto);
        model.addAttribute("usuarioLogado", logado);
        model.addAttribute("roles", Role.values());
        return "usuarios/formulario";
    }

    @PostMapping("/usuarios/salvar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public String salvarUsuario(@ModelAttribute("usuario") UsuarioDTO usuarioDTO,
                                @AuthenticationPrincipal UserDetails userDetails) {
        Usuario logado = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        usuarioService.salvar(usuarioDTO, logado);
        logService.registrarLog(userDetails.getUsername(), "SALVAR_USUARIO",
                "Usuário criado/editado: " + usuarioDTO.getUsername() + " com a role " + usuarioDTO.getRole());
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/resetar-senha")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public String resetarSenha(@RequestParam Long id,
                               @RequestParam String novaSenha,
                               @AuthenticationPrincipal UserDetails userDetails) {
        Usuario logado = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        usuarioService.resetarSenha(id, novaSenha, logado);
        logService.registrarLog(userDetails.getUsername(), "RESETAR_SENHA", "Redefiniu a senha do ID do usuário: " + id);
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/deletar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    public String deletarUsuario(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario logado = usuarioRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        usuarioService.deletar(id, logado);
        logService.registrarLog(userDetails.getUsername(), "DELETAR_USUARIO", "Excluiu o usuário ID: " + id);
        return "redirect:/usuarios";
    }
}