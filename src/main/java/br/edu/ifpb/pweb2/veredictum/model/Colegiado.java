package br.edu.ifpb.pweb2.veredictum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"membros", "reunioes", "processos"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Colegiado {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String descricao;
    private String portaria;

    @ManyToMany(mappedBy = "colegiados")
    private Set<Professor> membros = new HashSet<>();

    @OneToMany(mappedBy = "colegiado")
    private List<Reuniao> reunioes = new ArrayList<>();
}
