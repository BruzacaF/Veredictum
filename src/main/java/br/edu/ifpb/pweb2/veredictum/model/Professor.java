package br.edu.ifpb.pweb2.veredictum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Professor extends Usuario {
    private String matricula;
    private boolean coordenador;

    @ManyToMany(mappedBy = "membros")
    private Set<Colegiado> colegiados = new HashSet<>();

    @OneToMany(mappedBy = "relator")
    private List<Processo> processosRelatados = new ArrayList<>();
}
