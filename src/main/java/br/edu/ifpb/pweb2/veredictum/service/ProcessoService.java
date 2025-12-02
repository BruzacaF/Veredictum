package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTO;
import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTOFiltro;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.model.Aluno;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.repository.ProcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProcessoService {
    @Autowired
    ProcessoRepository processoRepository;
    @Autowired
    private UsuarioService usuarioService;

    public Processo criar(ProcessoDTO processoDTO, Aluno aluno) {
        try {
            Processo processo = new Processo();
            processo.setAssunto(processoDTO.getAssunto());
            processo.setTextoRequerimento(processoDTO.getTextoRequerimento());
            processo.setAluno(aluno);
            processo.setStatus(StatusProcessoEnum.CRIADO);
            processo.setDataCriacao(LocalDate.now());
            
            String ano = String.valueOf(LocalDate.now().getYear());
            Long sequencial = processoRepository.count() + 1;
            processo.setNumero(ano + "-" + String.format("%03d", sequencial));

            return processoRepository.save(processo);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar processo", e);
        }
    }

    public List<Processo> buscarPorAluno(Aluno aluno) {
        return processoRepository.findByAluno(aluno);
    }

    public List<Processo> buscarPorProfessorRelator(Professor professor) {
        return processoRepository.findByRelator(professor);
    }

    public List<Processo> listarProcessosDoColegiado(ProcessoDTOFiltro filtro, Long colegiadoId) {
        return processoRepository.filtrarPorColegiado(filtro, colegiadoId);
    }

    public long contarProcessosPorStatus(Long colegiadoId, StatusProcessoEnum status) {
        ProcessoDTOFiltro filtro = new ProcessoDTOFiltro();
        filtro.setStatus(status.name());
        
        return processoRepository.filtrarPorColegiado(filtro, colegiadoId).size();
    }
}
