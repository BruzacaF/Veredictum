package br.edu.ifpb.pweb2.veredictum.criteria;

import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTOFiltro;
import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.model.Processo;

import java.util.List;

public interface ProcessoRepositoryCustom {
    List<Processo> filtrar(ProcessoDTOFiltro filtro, Long alunoId, RoleEnum role);
}
