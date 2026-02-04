package br.edu.ifpb.pweb2.veredictum.criteria;

import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTOFiltro;
import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
@Repository
@RequiredArgsConstructor
public class ProcessoRepositoryImpl implements ProcessoRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Processo> filtrar(ProcessoDTOFiltro filtro, Long usuarioId, RoleEnum role, Pageable pageable) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // === QUERY PRINCIPAL ===
        CriteriaQuery<Processo> cq = cb.createQuery(Processo.class);
        Root<Processo> root = cq.from(Processo.class);

        List<Predicate> predicates = montarPredicates(cb, root, filtro, usuarioId, role);

        cq.where(predicates.toArray(new Predicate[0]));
        aplicarOrdenacao(cq, root, cb, filtro.getOrdenacao());

        TypedQuery<Processo> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Processo> processos = query.getResultList();

        // === QUERY DE COUNT ===
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Processo> countRoot = countQuery.from(Processo.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> countPredicates = montarPredicates(cb, countRoot, filtro, usuarioId, role);
        countQuery.where(countPredicates.toArray(new Predicate[0]));

        Long total = em.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(processos, pageable, total);
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

        // Filtro por nome do Aluno (LIKE case-insensitive)
        if (filtro.getNomeAluno() != null && !filtro.getNomeAluno().isBlank()) {
            predicates.add(
                    builder.like(
                            builder.lower(root.get("aluno").get("nome")),
                            "%" + filtro.getNomeAluno().toLowerCase() + "%"
                    )
            );
        }

        // Filtro por nome do Relator (LIKE case-insensitive)
        if (filtro.getNomeRelator() != null && !filtro.getNomeRelator().isBlank()) {
            predicates.add(
                    builder.like(
                            builder.lower(root.get("relator").get("nome")),
                            "%" + filtro.getNomeRelator().toLowerCase() + "%"
                    )
            );
        }

        // Filtros comuns
        adicionarFiltrosComuns(predicates, root, builder, filtro);

        criteria.where(predicates.toArray(new Predicate[0]));
        aplicarOrdenacao(criteria, root, builder, filtro.getOrdenacao());

        return em.createQuery(criteria).getResultList();
    }

    // ======= NOVO MÉTODO CENTRALIZADO DE FILTROS =======
    private List<Predicate> montarPredicates(CriteriaBuilder cb, Root<Processo> root,
                                             ProcessoDTOFiltro filtro, Long usuarioId, RoleEnum role) {

        List<Predicate> predicates = new ArrayList<>();

        // Filtro por usuário (aluno/professor)
        if (role == RoleEnum.ALUNO) {
            predicates.add(cb.equal(root.get("aluno").get("id"), usuarioId));
        } else if (role == RoleEnum.PROFESSOR) {
            predicates.add(cb.equal(root.get("relator").get("id"), usuarioId));
        }

        // Filtros comuns
        adicionarFiltrosComuns(predicates, root, cb, filtro);

        return predicates;
    }

    private void adicionarFiltrosComuns(List<Predicate> predicates, Root<Processo> root,
                                        CriteriaBuilder builder, ProcessoDTOFiltro filtro) {

        // Filtro por Assunto
        if (filtro.getAssunto() != null && !filtro.getAssunto().isEmpty()) {
            predicates.add(
                    builder.like(
                            builder.lower(root.get("assunto").get("nome")),
                            "%" + filtro.getAssunto().toLowerCase() + "%"
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
