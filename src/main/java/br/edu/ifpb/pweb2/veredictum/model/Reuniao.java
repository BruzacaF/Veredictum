package br.edu.ifpb.pweb2.veredictum.model;

import java.util.ArrayList;
import java.util.List;

class Reuniao {
    private Long id;
    private Date data;
    private Colegiado colegiado;
    private List<Processo> processos = new ArrayList<>();


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }


    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }


    public Colegiado getColegiado() { return colegiado; }
    public void setColegiado(Colegiado colegiado) { this.colegiado = colegiado; }


    public List<Processo> getProcessos() { return processos; }
    public void setProcessos(List<Processo> processos) { this.processos = processos; }
}