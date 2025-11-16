package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class HomeController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal UsuarioDetails usuarioDetails) {
        model.addAttribute("usuario", usuarioDetails.getUsuario());
        return "home";
    }



}
