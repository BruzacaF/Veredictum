package br.edu.ifpb.pweb2.veredictum.model;

import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.enums.TipoDecisao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"votos", "documentos", "relator", "aluno", "colegiado", "reuniao"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)  // ✅ Usar apenas campos explícitos
public class Processo {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include  // ✅ Incluir apenas o ID
    private Long id;

    @NotNull
    @EqualsAndHashCode.Include  // ✅ E o número único
    private String numero;
    
    @NotNull
    private LocalDate dataCriacao;
    private LocalDate dataDistribuicao;
    private LocalDate dataParecer;
    
    @NotBlank
    private String textoRequerimento;

    private String caminhoArquivo;

    @Lob
    private byte[] parecer;

    @Enumerated(EnumType.STRING)
    private TipoDecisao decisaoRelator;

    @ManyToOne
    @JoinColumn(name = "relator_id")
    private Professor relator;

    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "assunto_id")
    private Assunto assunto;

    @ManyToOne
    @JoinColumn(name = "colegiado_id")
    private Colegiado colegiado;

    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Voto> votos = new ArrayList<>();

    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Documento> documentos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private StatusProcessoEnum status;

    @ManyToOne
    @JoinColumn(name = "reuniao_id")
    private Reuniao reuniao;
}