package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.repository.AssuntoRepository;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.ProcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class HomeController {
    @Autowired
    private ProcessoService processoService;
    @Autowired
    private AssuntoRepository assuntoRepository;

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal UsuarioDetails usuarioDetails) {
        model.addAttribute("aluno", usuarioDetails.getUsuario());
        model.addAttribute("processos", processoService.buscarPorAluno(usuarioDetails.getUsuario()));
        model.addAttribute("processo", new Processo());
        model.addAttribute("assuntos", assuntoRepository.findAll());
        return "/aluno/dashboard";
    }





}
