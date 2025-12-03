package br.edu.ifpb.pweb2.veredictum.model;

import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Reuniao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime data;

    @Enumerated(EnumType.STRING)
    private StatusReuniao status;

    @Lob
    private byte[] ata;

    @ManyToOne
    private Colegiado colegiado;

    @ManyToMany
    @JoinTable(name = "reuniao_processo",
            joinColumns = @JoinColumn(name = "reuniao_id"),
            inverseJoinColumns = @JoinColumn(name = "processo_id"))
    private Set<Processo> pauta = new HashSet<>();
}