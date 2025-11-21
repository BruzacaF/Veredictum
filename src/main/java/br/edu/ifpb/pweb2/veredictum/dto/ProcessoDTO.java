package br.edu.ifpb.pweb2.veredictum.dto;

import br.edu.ifpb.pweb2.veredictum.model.Assunto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ProcessoDTO {

    @NotNull
    private Assunto assunto;

    @NotBlank
    private String textoRequerimento;
}
