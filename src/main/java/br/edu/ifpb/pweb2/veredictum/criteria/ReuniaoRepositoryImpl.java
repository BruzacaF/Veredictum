package br.edu.ifpb.pweb2.veredictum.criteria;

import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import br.edu.ifpb.pweb2.veredictum.model.Colegiado;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Reuniao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReuniaoRepositoryImpl implements ReuniaoRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Reuniao> buscarReunioesProfessor(
            Professor professor,
            StatusReuniao status,
            LocalDate data
    ) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Reuniao> cq = cb.createQuery(Reuniao.class);
        Root<Reuniao> reuniao = cq.from(Reuniao.class);

        Join<Reuniao, Colegiado> colegiado = reuniao.join("colegiado");
        Join<Colegiado, Professor> membros = colegiado.join("membros");

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(membros.get("id"), professor.getId()));

        if (status != null) {
            predicates.add(cb.equal(reuniao.get("status"), status));
        }

        if (data != null) {
            predicates.add(
                    cb.equal(
                            cb.function("date", LocalDate.class, reuniao.get("data")),
                            data
                    )
            );
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(reuniao.get("data")));

        return em.createQuery(cq).getResultList();
    }
}
