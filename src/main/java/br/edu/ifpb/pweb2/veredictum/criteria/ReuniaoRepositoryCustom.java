package br.edu.ifpb.pweb2.veredictum.criteria;

import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Reuniao;

import java.time.LocalDate;
import java.util.List;

public interface ReuniaoRepositoryCustom {
    List<Reuniao> buscarReunioesProfessor(
            Professor professor,
            StatusReuniao status,
            LocalDate data
    );
}
