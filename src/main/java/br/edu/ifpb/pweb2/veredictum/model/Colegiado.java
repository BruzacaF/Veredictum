package br.edu.ifpb.pweb2.veredictum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Colegiado {
    @Id @GeneratedValue
    private Long id;

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String descricao;
    private String portaria;

    @ManyToMany
    @JoinTable(name = "colegiado_professor",
            joinColumns = @JoinColumn(name = "colegiado_id"),
            inverseJoinColumns = @JoinColumn(name = "professor_id"))
    private Set<Professor> membros = new HashSet<>();

    @OneToMany(mappedBy = "colegiado")
    private List<Reuniao> reunioes = new ArrayList<>();
}
