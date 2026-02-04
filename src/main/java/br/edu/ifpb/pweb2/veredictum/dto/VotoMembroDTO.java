package br.edu.ifpb.pweb2.veredictum.dto;

import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Voto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VotoMembroDTO {
    private Professor membro;
    private Voto voto; // pode ser null se ainda não votou
}
