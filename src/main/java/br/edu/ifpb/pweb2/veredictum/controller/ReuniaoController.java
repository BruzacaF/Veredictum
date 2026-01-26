package br.edu.ifpb.pweb2.veredictum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
class ReuniaoController {


    @RequestMapping("/professor/Listar")
    public String listarReuniao() {


        return "professor/Listar";
    }

}
