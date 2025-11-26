package br.edu.ifpb.pweb2.veredictum.model;

import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Processo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assunto_id")
    private Assunto assunto;

    @NotBlank
    private String textoRequerimento;

    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Usuario aluno;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Usuario professor;

    @NotNull
    private LocalDate dataCriacao;

    @NotNull
    private String numeroProcesso;

    @ManyToOne
    @JoinColumn(name = "reuniao_id")
    private Reuniao reuniao; // reunião onde será julgado

    @Enumerated(EnumType.STRING)
    private StatusProcessoEnum status;


}