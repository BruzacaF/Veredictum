package br.edu.ifpb.pweb2.veredictum.controller.controllerAdvice;


import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTO;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.repository.AssuntoRepository;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.ProcessoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class DashboardControllerAdvice {
    @Autowired
    private ProcessoService processoService;
    @Autowired
    private AssuntoRepository assuntoRepository;

    @ModelAttribute
    public void addDashboardAttributes(HttpServletRequest request , Model model, @AuthenticationPrincipal UsuarioDetails
                                       usuario) {
        String uri = request.getRequestURI();

        if (uri.startsWith("/home")) {
            model.addAttribute("processoDTO", new ProcessoDTO());
            if (usuario != null) {
                model.addAttribute("usuario", usuario.getUsuario() );
                model.addAttribute("processos", processoService.buscarPorAluno(usuario.getUsuario()));

            }

            model.addAttribute("status", StatusProcessoEnum.values());
            model.addAttribute("assuntos", assuntoRepository.findAll());


        }
    }

}
