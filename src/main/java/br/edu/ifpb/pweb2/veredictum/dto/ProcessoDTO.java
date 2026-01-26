package br.edu.ifpb.pweb2.veredictum.dto;

import br.edu.ifpb.pweb2.veredictum.model.Assunto;
import br.edu.ifpb.pweb2.veredictum.validation.ArquivoValido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class ProcessoDTO {

    @NotNull(message = "É obrigatório escolher um assunto")
    private Assunto assunto;

    @NotBlank(message = "O texto do requerimento não pode estar vazio")
    private String textoRequerimento;

    @ArquivoValido(message = "Apenas arquivos PDF são permitidos")
    private MultipartFile arquivo;
}
