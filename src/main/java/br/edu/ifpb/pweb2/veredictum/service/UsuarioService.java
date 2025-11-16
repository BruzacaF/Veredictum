package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import br.edu.ifpb.pweb2.veredictum.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioDetailsService usuarioDetailsService;

    public Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email);

    }
}