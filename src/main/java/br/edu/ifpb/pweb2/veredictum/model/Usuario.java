package br.edu.ifpb.pweb2.veredictum.model;

import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
public class Usuario implements UserDetails {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Getter
    @Setter
    protected String nome;

    @Getter
    @Setter
    protected String email;
    @Setter
    @Getter
    protected String senha;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    protected RoleEnum role;

    @OneToMany(mappedBy = "aluno")
    private List<Processo> processos;

    @OneToMany(mappedBy = "professor")
    private List<Processo> processosProfessor;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return nome;
    }
}