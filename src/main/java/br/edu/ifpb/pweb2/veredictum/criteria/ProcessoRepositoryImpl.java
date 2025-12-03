package br.edu.ifpb.pweb2.veredictum.criteria;

import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTOFiltro;
import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProcessoRepositoryImpl implements ProcessoRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Processo> filtrar(ProcessoDTOFiltro filtro, Long usuarioId, RoleEnum role) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Processo> criteria = builder.createQuery(Processo.class);
        Root<Processo> root = criteria.from(Processo.class);

        List<Predicate> predicates = new ArrayList<>();

        // Filtro 1 - Usuário logado (Aluno ou Professor)
        if (usuarioId != null && role == RoleEnum.ALUNO) {
            predicates.add(builder.equal(root.get("aluno").get("id"), usuarioId));
        } else if (usuarioId != null && role == RoleEnum.PROFESSOR) {
            predicates.add(builder.equal(root.get("relator").get("id"), usuarioId));
        }

        adicionarFiltrosComuns(predicates, root, builder, filtro);

        criteria.where(predicates.toArray(new Predicate[0]));
        aplicarOrdenacao(criteria, root, builder, filtro.getOrdenacao());

        return em.createQuery(criteria).getResultList();
    }

    @Override
    public List<Processo> filtrarPorColegiado(ProcessoDTOFiltro filtro, Long colegiadoId) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Processo> criteria = builder.createQuery(Processo.class);
        Root<Processo> root = criteria.from(Processo.class);

        List<Predicate> predicates = new ArrayList<>();

        // Filtro por colegiado
        if (colegiadoId != null) {
            predicates.add(builder.equal(root.get("colegiado").get("id"), colegiadoId));
        }

        //  Filtro por nome do Aluno (LIKE case-insensitive)
        if (filtro.getNomeAluno() != null && !filtro.getNomeAluno().isBlank()) {
            predicates.add(
                builder.like(
                    builder.lower(root.get("aluno").get("nome")),
                    builder.literal("%" + filtro.getNomeAluno().toLowerCase() + "%")
                )
            );
        }

        // Filtro por nome do Relator (LIKE case-insensitive)
        if (filtro.getNomeRelator() != null && !filtro.getNomeRelator().isBlank()) {
            predicates.add(
                builder.like(
                    builder.lower(root.get("relator").get("nome")),
                    builder.literal("%" + filtro.getNomeRelator().toLowerCase() + "%")
                )
            );
        }

        // Filtros comuns
        adicionarFiltrosComuns(predicates, root, builder, filtro);

        criteria.where(predicates.toArray(new Predicate[0]));
        aplicarOrdenacao(criteria, root, builder, filtro.getOrdenacao());

        return em.createQuery(criteria).getResultList();
    }

    private void adicionarFiltrosComuns(List<Predicate> predicates, Root<Processo> root,
                                        CriteriaBuilder builder, ProcessoDTOFiltro filtro) {
        // Filtro por Assunto
        if (filtro.getAssunto() != null && !filtro.getAssunto().isEmpty()) {
            predicates.add(
                builder.like(
                    builder.lower(root.get("assunto").get("nome")),
                    builder.literal("%" + filtro.getAssunto().toLowerCase() + "%")
                )
            );
        }

        // Filtro por Status (Enum)
        if (filtro.getStatus() != null && !filtro.getStatus().isBlank()) {
            try {
                StatusProcessoEnum statusEnum = StatusProcessoEnum.valueOf(filtro.getStatus());
                predicates.add(builder.equal(root.get("status"), statusEnum));
            } catch (IllegalArgumentException e) {
                // Status inválido, não filtra
            }
        }
    }

    private void aplicarOrdenacao(CriteriaQuery<Processo> criteria, Root<Processo> root,
                                  CriteriaBuilder builder, String ordenacao) {
        if ("ASC".equalsIgnoreCase(ordenacao)) {
            criteria.orderBy(builder.asc(root.get("dataCriacao")));
        } else {
            criteria.orderBy(builder.desc(root.get("dataCriacao")));
        }
    }
}
