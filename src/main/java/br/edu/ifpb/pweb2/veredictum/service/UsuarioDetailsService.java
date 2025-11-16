package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import br.edu.ifpb.pweb2.veredictum.repository.UsuarioRepository;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email){
        Usuario usuario = usuarioRepository.findByEmail(email);

        return new UsuarioDetails(usuario);
    };

}
