package br.edu.ifpb.pweb2.veredictum.repository;

import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    
    boolean existsByProcessoIdAndProfessorId(Long processoId, Long professorId);
    
    List<Voto> findByProcessoId(Long processoId);
    
    Optional<Voto> findByProfessorAndProcesso(Professor professor, Processo processo);
    
    @Query("SELECT v FROM Voto v WHERE v.processo.id = :processoId AND v.professor.id = :professorId")
    Voto findByProcessoIdAndProfessorId(@Param("processoId") Long processoId, @Param("professorId") Long professorId);
    
    @Query("SELECT COUNT(v) FROM Voto v WHERE v.processo.id = :processoId")
    long countVotosByProcessoId(@Param("processoId") Long processoId);
}
