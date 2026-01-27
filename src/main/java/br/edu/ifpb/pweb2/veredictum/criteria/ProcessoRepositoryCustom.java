package br.edu.ifpb.pweb2.veredictum.criteria;

import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTOFiltro;
import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProcessoRepositoryCustom {
    Page<Processo> filtrar(ProcessoDTOFiltro filtro, Long usuarioId, RoleEnum role, Pageable pageable);
    List<Processo> filtrarPorColegiado(ProcessoDTOFiltro filtro, Long colegiadoId);
}
