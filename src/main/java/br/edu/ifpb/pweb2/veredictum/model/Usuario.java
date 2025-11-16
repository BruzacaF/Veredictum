package br.edu.ifpb.pweb2.veredictum.model;

import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    protected String nome;
    protected String email;
    protected String senha;
    @Enumerated(EnumType.STRING)
    protected RoleEnum role;



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }


    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }


    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public RoleEnum getRole() { return role; }
    public void setRole(RoleEnum role) { this.role = role; }

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