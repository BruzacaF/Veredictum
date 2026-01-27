package br.edu.ifpb.pweb2.veredictum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"membros"})
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

    @ManyToMany(mappedBy = "colegiados", fetch = FetchType.EAGER)
    private Set<Professor> membros = new HashSet<>();

    // Método auxiliar para obter IDs dos membros como String separada por vírgula
    public String getMembrosIdsAsString() {
        if (membros == null || membros.isEmpty()) {
            return "";
        }
        return membros.stream()
                .map(p -> String.valueOf(p.getId()))
                .collect(Collectors.joining(","));
    }
}
