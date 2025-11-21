package br.edu.ifpb.pweb2.veredictum.criteria;

import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTOFiltro;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProcessoRepositoryImpl implements ProcessoRepositoryCustom{


    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Processo> filtrar (ProcessoDTOFiltro filtro, Long alunoId) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Processo> criteria = builder.createQuery(Processo.class);
        Root<Processo> root = criteria.from(Processo.class);

        List<Predicate> predicates = new ArrayList<>();

        //Fitlro 1 - Aluno logado
        if (alunoId != null) {
            predicates.add(builder.equal(root.get("aluno").get("id"), alunoId));
        }

        //Filtro 2 - Assunto
        if(filtro.getAssunto() != null && !filtro.getAssunto().isEmpty()){
            predicates.add(
                    builder.like(
                            builder.lower(root.get("assunto").get("descricao")),
                            builder.literal("%" + filtro.getAssunto().toLowerCase() + "%")
                    )
            );
        }

        //Filtro 3 - Status (Enum) -> que agora é string no ProcessoDTOFiltro

        if (filtro.getStatus() != null && !filtro.getStatus().isBlank()) {
            try {
                StatusProcessoEnum statusEnum = StatusProcessoEnum.valueOf(filtro.getStatus());
                predicates.add(builder.equal(root.get("status"), statusEnum));
            } catch (IllegalArgumentException e) {
                // status inválido, não filtra
            }
        }

        criteria.where(predicates.toArray(new Predicate[0]));

        if ("ASC".equalsIgnoreCase(filtro.getOrdenacao())) {
            criteria.orderBy(builder.asc(root.get("dataCriacao")));
        } else {
            criteria.orderBy(builder.desc(root.get("dataCriacao")));

        }

        return em.createQuery(criteria).getResultList();
    }
}
