package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import br.edu.ifpb.pweb2.veredictum.model.Aluno;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Reuniao;
import br.edu.ifpb.pweb2.veredictum.repository.AssuntoRepository;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.ProcessoService;
import br.edu.ifpb.pweb2.veredictum.service.ReuniaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @Autowired
    private ReuniaoService reuniaoService;

    @GetMapping("/aluno")
    public String home(Model model, @AuthenticationPrincipal UsuarioDetails usuario) {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Processo> processosPage = processoService.buscarPorAlunoPaginado(
                (Aluno) usuario.getUsuario(),
                pageable
        );

        model.addAttribute("itensTabela", processosPage.getContent());
        model.addAttribute("paginaAtual", processosPage.getNumber());
        model.addAttribute("totalPaginas", processosPage.getTotalPages());

        return "aluno/dashboard";
    }


    @GetMapping("/professor")
    public String professor(Model model, @AuthenticationPrincipal UsuarioDetails usuario) {

        List<Processo> processos = processoService.buscarPorProfessorRelator((Professor) usuario.getUsuario());
        List<Reuniao>  reuniaoList = reuniaoService.buscarPorProfessor((Professor) usuario.getUsuario());
        model.addAttribute("itensTabela", processos);
        model.addAttribute("reunioes", reuniaoList);
        model.addAttribute("status", StatusReuniao.values());
        return "professor/dashboard";
    }



}
