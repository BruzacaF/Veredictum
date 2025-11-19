package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import br.edu.ifpb.pweb2.veredictum.service.ProcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
class ProcessoController {

    @Autowired
    ProcessoService processoService;

    @PostMapping("/adicionar")
    public String adicionarProcesso(@ModelAttribute Processo processo, RedirectAttributes redirectAttributes, @AuthenticationPrincipal Usuario aluno) {
        processoService.criar(processo,  aluno);
        redirectAttributes.addFlashAttribute("message", "Processo adicionado com sucesso!");

        return "redirect:/aluno/dashboard"; //Redirecionar para template apropriado depois
    }




}
