package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Reuniao;
import br.edu.ifpb.pweb2.veredictum.repository.ReuniaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReuniaoService {

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    public List<Reuniao>  buscarPorProfessor(Professor professor) {
        return reuniaoRepository.findByColegiado_Membros(professor);
    }

    public List<Reuniao> buscarReuniosProfessorFiltro(Professor professor, StatusReuniao statusReuniao, LocalDate data) {
        return reuniaoRepository.buscarReunioesProfessor(professor, statusReuniao, data);
    }

    public Reuniao buscarPorIdComPauta(Long id) {
        return reuniaoRepository.buscarDetalhes(id);

    }

}
