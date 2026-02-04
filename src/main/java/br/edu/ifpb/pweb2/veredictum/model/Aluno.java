package br.edu.ifpb.pweb2.veredictum.model;

import br.edu.ifpb.pweb2.veredictum.validation.MatriculaValida;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aluno extends Usuario {
    
    @NotBlank(message = "A matrícula é obrigatória")
    @MatriculaValida(message = "Matrícula inválida. Use o formato YYYYNNNN (ex: 20221001)")
    private String matricula;
}

