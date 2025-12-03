package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.model.Aluno;
import br.edu.ifpb.pweb2.veredictum.model.Assunto;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import br.edu.ifpb.pweb2.veredictum.model.Colegiado;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.AssuntoService;
import br.edu.ifpb.pweb2.veredictum.service.UsuarioService;
import br.edu.ifpb.pweb2.veredictum.service.ColegiadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AssuntoService assuntoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ColegiadoService colegiadoService;

    @GetMapping
    public String home(@AuthenticationPrincipal UsuarioDetails userDetails, Model model) {
        model.addAttribute("usuario", userDetails.getUsuario());
        model.addAttribute("assuntos", assuntoService.listarTodos());
        model.addAttribute("totalAssuntos", assuntoService.contarAssuntos());
        model.addAttribute("totalColegiados", colegiadoService.contarColegiados());
        model.addAttribute("colegiados", colegiadoService.listarTodos());
        model.addAttribute("professores", colegiadoService.listarProfessoresDisponiveis());
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("totalUsuarios", usuarioService.contarUsuarios());
        model.addAttribute("totalProcessos", 0);
        model.addAttribute("roles", RoleEnum.values());
        return "admin/home-admin";
    }

    // ==================== ASSUNTOS ====================
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

    // ==================== USUÁRIOS ====================
    @GetMapping("/usuario")
    public String listarUsuarios(@AuthenticationPrincipal UsuarioDetails userDetails, Model model) {
        model.addAttribute("usuario", userDetails.getUsuario());
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("roles", RoleEnum.values());
        return "admin/home-admin";
    }

    @PostMapping("/usuario")
    public String criarUsuario(@ModelAttribute Usuario usuario,
                               @RequestParam(required = false) String matricula,
                               RedirectAttributes redirectAttributes) {
        try {
            if (usuarioService.emailExiste(usuario.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "Email já cadastrado!");
                return "redirect:/admin";
            }
            usuarioService.salvar(usuario, matricula);
            redirectAttributes.addFlashAttribute("success", "Usuário criado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao criar usuário: " + e.getMessage());
        }
        return "redirect:/admin/usuario";
    }

    @PostMapping("/usuario/update")
    public String atualizarUsuario(@RequestParam Long id,
                                   @ModelAttribute Usuario usuario,
                                   @RequestParam(required = false) String matricula,
                                   RedirectAttributes redirectAttributes) {
        try {
            if (usuarioService.emailExiste(usuario.getEmail(), id)) {
                redirectAttributes.addFlashAttribute("error", "Email já cadastrado para outro usuário!");
                return "redirect:/admin";
            }
            usuarioService.atualizar(id, usuario, matricula);
            redirectAttributes.addFlashAttribute("success", "Usuário atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar usuário: " + e.getMessage());
        }
        return "redirect:/admin/usuario";
    }

    @PostMapping("/usuario/delete")
    public String excluirUsuario(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.excluir(id);
            redirectAttributes.addFlashAttribute("success", "Usuário excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir usuário: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @GetMapping("/usuario/{id}")
    @ResponseBody
    public Usuario buscarUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            // Limpar senha por segurança
            u.setSenha(null);
            return u;
        }
        return null;
    }

    @GetMapping("/usuario/{id}/matricula")
    @ResponseBody
    public String buscarMatricula(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            if (u instanceof Aluno) {
                return ((Aluno) u).getMatricula();
            } else if (u instanceof Professor) {
                return ((Professor) u).getMatricula();
            }
        }
        return "";
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
