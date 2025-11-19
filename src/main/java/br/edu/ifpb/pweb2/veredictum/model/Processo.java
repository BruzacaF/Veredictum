package br.edu.ifpb.pweb2.veredictum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Processo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assunto_id")
    private Assunto assunto;

    private String textoRequerimento;

    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Usuario aluno;

    @ManyToOne
    @JoinColumn(name = "reuniao_id")
    private Reuniao reuniao; // reunião onde será julgado


    ;


}