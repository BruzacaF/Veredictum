package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.model.Assunto;
import br.edu.ifpb.pweb2.veredictum.model.Colegiado;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.AssuntoService;
import br.edu.ifpb.pweb2.veredictum.service.ColegiadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AssuntoService assuntoService;

    @Autowired
    private ColegiadoService colegiadoService;

    @GetMapping
    public String home(@AuthenticationPrincipal UsuarioDetails userDetails, Model model) {
        model.addAttribute("usuario", userDetails.getUsuario());
        model.addAttribute("assuntos", assuntoService.listarTodos());
        model.addAttribute("totalAssuntos", assuntoService.contarAssuntos());
        model.addAttribute("totalUsuarios", 0); // TODO: implementar quando tiver UsuarioService
        model.addAttribute("totalProcessos", 0); // TODO: implementar quando tiver ProcessoService
        model.addAttribute("totalColegiados", colegiadoService.contarColegiados());
        model.addAttribute("colegiados", colegiadoService.listarTodos());
        model.addAttribute("professores", colegiadoService.listarProfessoresDisponiveis());
        return "admin/home-admin";
    }

    // ==================== ASSUNTO ====================

    @PostMapping("/assunto")
    public String criarAssunto(@ModelAttribute Assunto assunto, RedirectAttributes redirectAttributes) {
        try {
            assuntoService.salvar(assunto);
            redirectAttributes.addFlashAttribute("success", "Assunto criado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao criar assunto: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/assunto/update")
    public String atualizarAssunto(@ModelAttribute Assunto assunto, RedirectAttributes redirectAttributes) {
        try {
            assuntoService.atualizar(assunto);
            redirectAttributes.addFlashAttribute("success", "Assunto atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar assunto: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/assunto/delete")
    public String excluirAssunto(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            assuntoService.excluir(id);
            redirectAttributes.addFlashAttribute("success", "Assunto excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir assunto: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    // ==================== COLEGIADO ====================

    @PostMapping("/colegiado")
    public String criarColegiado(@ModelAttribute Colegiado colegiado,
                                  BindingResult bindingResult,
                                  @RequestParam(value = "membrosIds", required = false) List<Long> membrosIds,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Erro de validação: verifique os dados informados (data inválida?)");
            return "redirect:/admin";
        }
        try {
            colegiadoService.salvar(colegiado, membrosIds);
            redirectAttributes.addFlashAttribute("success", "Colegiado criado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao criar colegiado: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/colegiado/update")
    public String atualizarColegiado(@ModelAttribute Colegiado colegiado,
                                      BindingResult bindingResult,
                                      @RequestParam(value = "membrosIds", required = false) List<Long> membrosIds,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Erro de validação: verifique os dados informados (data inválida?)");
            return "redirect:/admin";
        }
        try {
            colegiadoService.atualizar(colegiado, membrosIds);
            redirectAttributes.addFlashAttribute("success", "Colegiado atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar colegiado: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/colegiado/delete")
    public String excluirColegiado(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            colegiadoService.excluir(id);
            redirectAttributes.addFlashAttribute("success", "Colegiado excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir colegiado: " + e.getMessage());
        }
        return "redirect:/admin";
    }
}
