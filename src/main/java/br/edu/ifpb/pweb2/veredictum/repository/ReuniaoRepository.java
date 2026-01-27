package br.edu.ifpb.pweb2.veredictum.repository;

import br.edu.ifpb.pweb2.veredictum.criteria.ReuniaoRepositoryCustom;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Reuniao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReuniaoRepository extends JpaRepository<Reuniao, Long>, ReuniaoRepositoryCustom {

    List<Reuniao> findByColegiado_Membros(Professor professor);

    @Query("""
        select r from Reuniao r
        left join fetch r.colegiado
        left join fetch r.pauta p
        left join fetch p.assunto
        where r.id = :id
    """)
    Reuniao buscarDetalhes(@Param("id") Long id);
}
