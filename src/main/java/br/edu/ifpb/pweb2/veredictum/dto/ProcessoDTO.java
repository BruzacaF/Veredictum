package br.edu.ifpb.pweb2.veredictum.dto;

import br.edu.ifpb.pweb2.veredictum.model.Assunto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ProcessoDTO {

    @NotNull(message = "É obrigatório escolher um assunto")
    private Assunto assunto;

    @NotBlank(message = "O texto do requerimento não pode estar vazio")
    private String textoRequerimento;
}
