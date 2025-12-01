package br.edu.ifpb.pweb2.veredictum.repository;

import br.edu.ifpb.pweb2.veredictum.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Professor findByEmail(String email);
    Professor findByMatricula(String matricula);
    List<Professor> findByEhCoordenadorTrue();
}