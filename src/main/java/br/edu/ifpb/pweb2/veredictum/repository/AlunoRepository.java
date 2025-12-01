package br.edu.ifpb.pweb2.veredictum.repository;

import br.edu.ifpb.pweb2.veredictum.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Aluno findByEmail(String email);
    Aluno findByMatricula(String matricula);
}