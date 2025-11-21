package br.edu.ifpb.pweb2.veredictum.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Entity
@Data
public class Reuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private Date data;

    @ManyToOne
    @JoinColumn(name = "colegiado_id")
    private Colegiado colegiado;

    @OneToMany(mappedBy = "reuniao")
    private List<Processo> processos = new ArrayList<>();


}