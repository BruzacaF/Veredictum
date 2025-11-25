package br.edu.ifpb.pweb2.veredictum.repository;

import br.edu.ifpb.pweb2.veredictum.criteria.ProcessoRepositoryCustom;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessoRepository extends JpaRepository<Processo, Long>, ProcessoRepositoryCustom {

    List<Processo> findByAluno(Usuario aluno);

    List<Processo> findByProfessor(Usuario professor);

}
