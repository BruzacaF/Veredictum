package br.edu.ifpb.pweb2.veredictum.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aluno extends Usuario {
    private String matricula;
}

