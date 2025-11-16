package br.edu.ifpb.pweb2.veredictum.model;

public class Processo {
    private Long id;
    private String assunto;
    private String textoRequerimento;
    private Usuario aluno;
    private Reuniao reuniao; // reunião onde será julgado


    public Long getId() { return id; }
    public String getAssunto() { return assunto; }
    public String getTextoRequerimento() { return textoRequerimento; }
    public Usuario getAluno() { return aluno; }
    public Reuniao getReuniao() { return reuniao; }

    public void setId(Long id) { this.id = id; }
    public void setAssunto(String assunto) { this.assunto = assunto; }
    public String setTextoRequerimento(String textoRequerimento) {

        return this.textoRequerimento = textoRequerimento;
    };
    public void setAluno(Usuario aluno) { this.aluno = aluno; }
    public Reuniao setReuniao(Reuniao reuniao) {
        return reuniao;
    }


}