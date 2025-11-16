package br.edu.ifpb.pweb2.veredictum.repository;

import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
}