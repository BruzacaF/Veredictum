package br.edu.ifpb.pweb2.veredictum.security;

import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                       HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException {
        
        UsuarioDetails usuarioDetails = (UsuarioDetails) authentication.getPrincipal();
        Usuario usuario = usuarioDetails.getUsuario();
        RoleEnum role = usuario.getRole();

        String redirectUrl;

        switch (role) {
            case ALUNO:
                redirectUrl = "/home/aluno";
                break;
            case PROFESSOR:
                redirectUrl = "/home/professor";
                break;
            case COORDENADOR:
                redirectUrl = "/coordenador/processos";
                break;
            case ADMIN:
                redirectUrl = "/admin";
                break;
            default:
                redirectUrl = "/home";
                break;
        }

        response.sendRedirect(redirectUrl);
    }
}