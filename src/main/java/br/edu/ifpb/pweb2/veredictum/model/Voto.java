package br.edu.ifpb.pweb2.veredictum.model;

import br.edu.ifpb.pweb2.veredictum.enums.TipoVoto;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Voto {
    @Id @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoVoto voto;

    @Lob
    private String justificativa;

    @ManyToOne
    private Professor professor;

    @ManyToOne
    private Processo processo;
}