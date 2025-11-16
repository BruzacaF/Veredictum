package br.edu.ifpb.pweb2.veredictum.model;

import java.util.ArrayList;
import java.util.List;

class Colegiado {
    private Long id;
    private String nome;
    private List<Professor> professores = new ArrayList<>();


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }


    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }


    public List<Professor> getProfessores() { return professores; }
    public void setProfessores(List<Professor> professores) { this.professores = professores; }
}