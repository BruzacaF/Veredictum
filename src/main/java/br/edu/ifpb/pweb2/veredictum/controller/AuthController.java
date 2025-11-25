package br.edu.ifpb.pweb2.veredictum.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {


    @GetMapping("/login")
    public String login() {
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
