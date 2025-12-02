package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.repository.AssuntoRepository;
import br.edu.ifpb.pweb2.veredictum.service.ProcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping("/home")
public class HomeController {
    @Autowired
    private ProcessoService processoService;
    @Autowired
    private AssuntoRepository assuntoRepository;

    @GetMapping("/aluno")
    public String home(Model model) {

        return "aluno/dashboard";
    }
}
