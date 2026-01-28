package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Reuniao;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.ReuniaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reuniao")
class ReuniaoController {

    @Autowired
    private final ReuniaoService reuniaoService;

    ReuniaoController(ReuniaoService reuniaoService) {
        this.reuniaoService = reuniaoService;
    }

    @GetMapping("/professor/listar")
    @Transactional(readOnly = true)
    public String listarReuniaoProfessor(
            @AuthenticationPrincipal UsuarioDetails usuario,
            @RequestParam(required = false) StatusReuniao status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            Model model
    ) {
        try {
            Professor professor = (Professor) usuario.getUsuario();
            List<Reuniao> reunioes = reuniaoService.buscarReuniosProfessorFiltro(professor, status, data);
            
            model.addAttribute("reunioes", reunioes);
            model.addAttribute("status", StatusReuniao.values());
            model.addAttribute("dataFiltro", data);
            model.addAttribute("statusFiltro", status);

            return "fragments/painel-reuniao :: painel-reuniao";
        } catch (Exception e) {
            model.addAttribute("reunioes", List.of());
            model.addAttribute("status", StatusReuniao.values());
            model.addAttribute("error", "Erro ao carregar reuniões: " + e.getMessage());
            return "fragments/painel-reuniao :: painel-reuniao";
        }
    }

    @GetMapping("/{id}/modal")
    @Transactional(readOnly = true)
    public String detalhesModal(@PathVariable Long id, Model model) {
        try {
            Reuniao reuniao = reuniaoService.buscarPorIdComPauta(id);
            if (reuniao == null) {
                throw new RuntimeException("Reunião não encontrada");
            }
            model.addAttribute("reuniao", reuniao);
            return "fragments/modal-reuniao :: conteudo";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "fragments/modal-reuniao :: erro";
        }
    }

    @PostMapping("/{id}/iniciar")
    @Transactional
    public String iniciarSessao(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails usuario,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Professor coordenador = (Professor) usuario.getUsuario();
            Reuniao sessao = reuniaoService.buscarPorId(id);
            
            if (sessao == null) {
                throw new RuntimeException("Sessão não encontrada");
            }
            
            if (!sessao.getCoordenador().getId().equals(coordenador.getId())) {
                throw new RuntimeException("Acesso negado. Apenas o coordenador responsável pode iniciar esta sessão.");
            }
            
            if (sessao.getStatus() != StatusReuniao.PROGRAMADA) {
                throw new RuntimeException("Apenas sessões programadas podem ser iniciadas.");
            }
            
            reuniaoService.iniciarSessao(id);
            redirectAttributes.addFlashAttribute("success", "✅ Sessão iniciada com sucesso!");
            return "redirect:/coordenador/sessao/" + id;
            
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "⚠️ " + e.getMessage());
            return "redirect:/coordenador/sessoes";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "❌ Erro ao iniciar sessão: " + e.getMessage());
            return "redirect:/coordenador/sessoes";
        }
    }
}
