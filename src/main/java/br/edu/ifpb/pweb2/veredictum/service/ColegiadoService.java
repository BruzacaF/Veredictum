package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.model.Colegiado;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.repository.ColegiadoRepository;
import br.edu.ifpb.pweb2.veredictum.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ColegiadoService {

    @Autowired
    private ColegiadoRepository colegiadoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    public List<Colegiado> listarTodos() {
        return colegiadoRepository.findAll();
    }

    public Colegiado buscarPorId(Long id) {
        return colegiadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colegiado não encontrado"));
    }

    @Transactional
    public Colegiado salvar(Colegiado colegiado, List<Long> membrosIds) {
        if (membrosIds != null && !membrosIds.isEmpty()) {
            Set<Professor> membros = new HashSet<>(professorRepository.findAllById(membrosIds));
            colegiado.setMembros(membros);
            // Adicionar o colegiado aos professores
            for (Professor professor : membros) {
                professor.getColegiados().add(colegiado);
            }
        }
        return colegiadoRepository.save(colegiado);
    }

    @Transactional
    public Colegiado atualizar(Colegiado colegiado, List<Long> membrosIds) {
        Colegiado existente = buscarPorId(colegiado.getId());
        
        existente.setDataInicio(colegiado.getDataInicio());
        existente.setDataFim(colegiado.getDataFim());
        existente.setDescricao(colegiado.getDescricao());
        existente.setPortaria(colegiado.getPortaria());

        // Remover colegiado dos membros antigos
        for (Professor professor : existente.getMembros()) {
            professor.getColegiados().remove(existente);
        }
        existente.getMembros().clear();

        // Adicionar novos membros
        if (membrosIds != null && !membrosIds.isEmpty()) {
            Set<Professor> novosMembros = new HashSet<>(professorRepository.findAllById(membrosIds));
            existente.setMembros(novosMembros);
            for (Professor professor : novosMembros) {
                professor.getColegiados().add(existente);
            }
        }

        return colegiadoRepository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        Colegiado colegiado = buscarPorId(id);
        // Remover relacionamento com professores
        for (Professor professor : colegiado.getMembros()) {
            professor.getColegiados().remove(colegiado);
        }
        colegiadoRepository.delete(colegiado);
    }

    public long contarColegiados() {
        return colegiadoRepository.count();
    }

    public List<Professor> listarProfessoresDisponiveis() {
        return professorRepository.findAllByOrderByNomeAsc();
    }
}
