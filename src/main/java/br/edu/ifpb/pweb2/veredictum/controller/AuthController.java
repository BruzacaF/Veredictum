package br.edu.ifpb.pweb2.veredictum.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {

        String error = (String) request.getSession().getAttribute("LOGIN_ERROR");

        if (error != null) {
            model.addAttribute("loginError", error);
            request.getSession().removeAttribute("LOGIN_ERROR");
        }

        return "login";
    }




    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("name", auth.getName());
        response.put("principal", auth.getPrincipal());
        response.put("authorities", auth.getAuthorities());
        response.put("details", auth.getDetails());
        response.put("authenticated", auth.isAuthenticated());

        return ResponseEntity.ok(response);
    }


}
