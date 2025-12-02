package br.edu.ifpb.pweb2.veredictum.repository;

import br.edu.ifpb.pweb2.veredictum.model.Colegiado;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Professor findByEmail(String email);
    Professor findByMatricula(String matricula);
    List<Professor> findByEhCoordenadorTrue();

    @Query("SELECT p " +
            "FROM Professor p " +
            "JOIN p.colegiados c " +
            "WHERE c.id = :colegiadoId " +
            "AND p.ehCoordenador = FALSE")
    List<Professor> findByColegiadoId(@Param("colegiadoId") Long colegiadoId);

    List<Professor> findByColegiadosContaining(Colegiado colegiado);
}