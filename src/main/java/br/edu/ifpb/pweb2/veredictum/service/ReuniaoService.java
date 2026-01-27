package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import br.edu.ifpb.pweb2.veredictum.model.Colegiado;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Reuniao;
import br.edu.ifpb.pweb2.veredictum.repository.ColegiadoRepository;
import br.edu.ifpb.pweb2.veredictum.repository.ProcessoRepository;
import br.edu.ifpb.pweb2.veredictum.repository.ReuniaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReuniaoService {

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    @Autowired
    private ColegiadoRepository colegiadoRepository;

    @Autowired
    private ProcessoRepository processoRepository;

    public List<Reuniao>  buscarPorProfessor(Professor professor) {
        return reuniaoRepository.findByColegiado_Membros(professor);
    }

    public List<Reuniao> buscarReuniosProfessorFiltro(Professor professor, StatusReuniao statusReuniao, LocalDate data) {
        return reuniaoRepository.buscarReunioesProfessor(professor, statusReuniao, data);
    }

    public Reuniao buscarPorIdComPauta(Long id) {
        return reuniaoRepository.buscarDetalhes(id);

    }

    @Transactional
    public Reuniao criarSessao(Long colegiadoId, LocalDateTime dataHora,
                               List<Long> processosIds, List<Long> professoresIds) {

        Colegiado colegiado = colegiadoRepository.findById(colegiadoId)
                .orElseThrow(() -> new RuntimeException("Colegiado não encontrado"));

        // Obter o coordenador do colegiado
        Professor coordenador = colegiado.getMembros().stream()
                .filter(Professor::isEhCoordenador)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Colegiado não possui coordenador"));

        Reuniao reuniao = new Reuniao();
        reuniao.setData(dataHora);
        reuniao.setStatus(StatusReuniao.PROGRAMADA);
        reuniao.setCoordenador(coordenador);
        reuniao.setPauta(new HashSet<>());
        reuniao.setMembros(new HashSet<>());

        // Adicionar coordenador automaticamente aos membros
        reuniao.getMembros().add(coordenador);

        // Adicionar outros membros selecionados
        if (professoresIds != null && !professoresIds.isEmpty()) {
            Set<Professor> membros = colegiado.getMembros().stream()
                    .filter(p -> professoresIds.contains(p.getId()))
                    .collect(java.util.stream.Collectors.toSet());
            reuniao.getMembros().addAll(membros);
        }

        if (processosIds != null && !processosIds.isEmpty()) {
            List<Processo> processos = processoRepository.findAllById(processosIds);

            for (Processo processo : processos) {
                processo.setStatus(StatusProcessoEnum.EM_PAUTA);
                processo.setReuniao(reuniao);
                reuniao.getPauta().add(processo);
            }
        }

        return reuniaoRepository.save(reuniao);
    }

    public List<Reuniao> listarPorCoordenador(Professor professor){
        return reuniaoRepository.findByCoordenador(professor);
    }
}
