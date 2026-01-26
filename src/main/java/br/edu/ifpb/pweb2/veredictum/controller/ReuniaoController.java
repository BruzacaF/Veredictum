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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping
    @RequestMapping("/professor/Listar")
    public String listarReuniao(
            @AuthenticationPrincipal UsuarioDetails usuario,
            @RequestParam(required = false)StatusReuniao status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate data, Model model
            ) {

        Professor professor = (Professor) usuario.getUsuario();
        List<Reuniao> reunioes = reuniaoService.buscarReuniosProfessorFiltro(professor, status, data);
        model.addAttribute("reunioes", reunioes);
        model.addAttribute("status", StatusReuniao.values());
        model.addAttribute("dataFiltro", data);
        model.addAttribute("StatusFiltro", status);

        return "fragments/painel-reuniao :: painel-reuniao";
    }



}
