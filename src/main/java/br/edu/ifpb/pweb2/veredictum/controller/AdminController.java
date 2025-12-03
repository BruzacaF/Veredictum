package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.model.Assunto;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.AssuntoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AssuntoService assuntoService;

    @GetMapping
    public String home(@AuthenticationPrincipal UsuarioDetails userDetails, Model model) {
        model.addAttribute("usuario", userDetails.getUsuario());
        model.addAttribute("assuntos", assuntoService.listarTodos());
        model.addAttribute("totalAssuntos", assuntoService.contarAssuntos());
        model.addAttribute("totalUsuarios", 0); // TODO: implementar quando tiver UsuarioService
        model.addAttribute("totalProcessos", 0); // TODO: implementar quando tiver ProcessoService
        model.addAttribute("totalColegiados", 0); // TODO: implementar quando tiver ColegiadoService
        return "admin/home-admin";
    }

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
}
