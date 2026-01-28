package br.edu.ifpb.pweb2.veredictum.repository;

import br.edu.ifpb.pweb2.veredictum.model.Colegiado;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByEmail(String email);
    Professor findByMatricula(String matricula);
    List<Professor> findByEhCoordenadorTrue();

    @Query("SELECT p " +
            "FROM Professor p " +
            "JOIN p.colegiados c " +
            "WHERE c.id = :colegiadoId " +
            "AND p.ehCoordenador = FALSE")
    List<Professor> findByColegiadoId(@Param("colegiadoId") Long colegiadoId);

    List<Professor> findByColegiadosContaining(Colegiado colegiado);

    List<Professor> findAllByOrderByNomeAsc();

    @Query("SELECT p FROM Professor p WHERE p.ehCoordenador = true")
    List<Professor> findAllCoordenadores();
}