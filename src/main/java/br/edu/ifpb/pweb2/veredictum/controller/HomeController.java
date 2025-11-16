package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.service.ProcessoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {


    private final ProcessoService processoService;

    public HomeController(ProcessoService processoService) {
        this.processoService = processoService;
    }

    @GetMapping
    public String home(){
        return "homePage";
    }

    @GetMapping("/aluno/dashboard")
    public String alunoDashboard(Model model){
        Aluno aluno = alunoService.getAlunoLogado();
        List<Processo> processos = processoService.buscarPorAluno(aluno.getId());
        model.addAttribute("processos", processos);
        model.addAttribute("aluno", aluno);
        model.addAttribute("processos", new Processo());

        return "/aluno/dashboard";
    }

}
