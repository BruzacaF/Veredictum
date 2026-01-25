package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.model.Aluno;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.repository.AssuntoRepository;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.ProcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller

@RequestMapping("/home")
public class HomeController {
    @Autowired
    private ProcessoService processoService;
    @Autowired
    private AssuntoRepository assuntoRepository;

    @GetMapping("/aluno")
    public String home(Model model, @AuthenticationPrincipal UsuarioDetails usuario) {

        List<Processo> processos = processoService.buscarPorAluno((Aluno) usuario.getUsuario());
        model.addAttribute("processos", processos);

        return "aluno/dashboard";
    }

    @GetMapping("/professor")
    public String professor(Model model, @AuthenticationPrincipal UsuarioDetails usuario) {

        List<Processo> processos = processoService.buscarPorProfessorRelator((Professor) usuario.getUsuario());
        model.addAttribute("processos", processos);
        return "professor/dashboard";
    }



}
