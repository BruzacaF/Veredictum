package br.edu.ifpb.pweb2.veredictum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeArquivo;
    
    private String tipoArquivo;
    
    private Long tamanho;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] conteudo;
    
    private LocalDateTime dataUpload;
    
    @ManyToOne
    @JoinColumn(name = "processo_id")
    private Processo processo;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario uploadPor;
}