package br.edu.ifpb.pweb2.veredictum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"colegiados", "processosRelatados", "reunioesCoordenadas", "reunioesParticipadas"}, callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Professor extends Usuario {
    
    @EqualsAndHashCode.Include
    private String matricula;
    
    private boolean ehCoordenador;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "professor_colegiado",
            joinColumns = @JoinColumn(name = "professor_id"),
            inverseJoinColumns = @JoinColumn(name = "colegiado_id")
    )
    private Set<Colegiado> colegiados = new HashSet<>();

    @OneToMany(mappedBy = "relator")
    private List<Processo> processosRelatados = new ArrayList<>();

    @OneToMany(mappedBy = "coordenador")
    private List<Reuniao> reunioesCoordenadas = new ArrayList<>();

    @ManyToMany(mappedBy = "membros")
    private Set<Reuniao> reunioesParticipadas = new HashSet<>();
}
