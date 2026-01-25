package br.edu.ifpb.pweb2.veredictum.controller.controllerAdvice;


import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.model.Assunto;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import br.edu.ifpb.pweb2.veredictum.repository.AssuntoRepository;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class DashboardControllerAdvice {

    private final AssuntoRepository assuntoRepository;

    public DashboardControllerAdvice(
                                     AssuntoRepository assuntoRepository) {

        this.assuntoRepository = assuntoRepository;
    }

    @ModelAttribute("usuario")
    public Usuario usuarioLogado(@AuthenticationPrincipal UsuarioDetails principal) {
        return principal != null ? principal.getUsuario() : null;
    }

    @ModelAttribute("statusProcesso")
    public StatusProcessoEnum[] statusProcesso() {
        return StatusProcessoEnum.values();
    }

    @ModelAttribute("assuntos")
    public List<Assunto> assuntos() {
        return assuntoRepository.findAll();
    }

    @ModelAttribute("processos")
    public List<Processo> processosPadrao() {
        return Collections.emptyList();
    }

    @ModelAttribute("isProfessor")
    public boolean isProfessor(@AuthenticationPrincipal UsuarioDetails usuario) {
        if (usuario != null) {
            return usuario.getUsuario().getRole() == RoleEnum.PROFESSOR;
        }
        return false;
    }

    @ModelAttribute("isAluno")
    public boolean isAluno(@AuthenticationPrincipal UsuarioDetails usuario) {
        if (usuario != null) {
            return usuario.getUsuario().getRole() == RoleEnum.ALUNO;
        }
        return false;
    }
}
