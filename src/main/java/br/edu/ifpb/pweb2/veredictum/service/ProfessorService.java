package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {
    @Autowired
    ProfessorRepository professorRepository;

    public List<Professor> buscarPorColegiadoId(Long colegiadoId){
        return professorRepository.findByColegiadoId(colegiadoId);
    }
}
