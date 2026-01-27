package br.edu.ifpb.pweb2.veredictum.repository;

import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Reuniao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {
    
    List<Reuniao> findByCoordenador(Professor coordenador);

    @Query("SELECT r FROM Reuniao r WHERE r.coordenador.id IN " +
           "(SELECT p.id FROM Professor p JOIN p.colegiados c WHERE c.id = :colegiadoId) " +
           "ORDER BY r.data DESC")
    List<Reuniao> findByCoordenadorColegiadosId(@Param("colegiadoId") Long colegiadoId);

    @Query("SELECT r FROM Reuniao r WHERE :professor MEMBER OF r.membros OR r.coordenador = :professor ORDER BY r.data DESC")
    List<Reuniao> findByColegiado_Membros(@Param("professor") Professor professor);

    @Query("SELECT DISTINCT r FROM Reuniao r " +
           "LEFT JOIN FETCH r.pauta " +
           "WHERE (:professor MEMBER OF r.membros OR r.coordenador = :professor) " +
           "AND (:status IS NULL OR r.status = :status) " +
           "AND (:data IS NULL OR CAST(r.data AS date) = :data) " +
           "ORDER BY r.data DESC")
    List<Reuniao> buscarReunioesProfessor(
            @Param("professor") Professor professor,
            @Param("status") StatusReuniao status,
            @Param("data") LocalDate data
    );

    @Query("SELECT r FROM Reuniao r " +
           "LEFT JOIN FETCH r.pauta p " +
           "LEFT JOIN FETCH r.membros " +
           "LEFT JOIN FETCH r.coordenador " +
           "LEFT JOIN FETCH p.aluno " +
           "LEFT JOIN FETCH p.assunto " +
           "LEFT JOIN FETCH p.relator " +
           "WHERE r.id = :id")
    Reuniao buscarDetalhes(@Param("id") Long id);
}
