package br.edu.ifpb.pweb2.veredictum.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProcessoDTOFiltro {
    private String assunto;
    private String status;
    private String ordenacao;
    private String nomeAluno;
    private String nomeRelator;
    private Long colegiadoId;
}
