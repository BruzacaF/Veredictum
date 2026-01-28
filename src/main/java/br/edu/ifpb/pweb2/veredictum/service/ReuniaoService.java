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

    public Reuniao buscarPorId(Long id) {
        return reuniaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada"));
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

    @Transactional
    public void excluirSessao(Long id) {
        Reuniao reuniao = reuniaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada"));
        
        // Remover processos da pauta e atualizar seus status
        if (reuniao.getPauta() != null && !reuniao.getPauta().isEmpty()) {
            for (Processo processo : reuniao.getPauta()) {
                processo.setReuniao(null);
                if (processo.getStatus() == StatusProcessoEnum.EM_PAUTA) {
                    processo.setStatus(StatusProcessoEnum.DISTRIBUIDO);
                }
                processoRepository.save(processo);
            }
        }
        
        reuniaoRepository.delete(reuniao);
    }

    public Reuniao iniciarSessao(Long reuniaoId) {
        Reuniao reuniao = buscarPorId(reuniaoId);
        
        List<Reuniao> reunioesEmAndamento = reuniaoRepository.findByStatus(StatusReuniao.EM_ANDAMENTO);
        
        if (!reunioesEmAndamento.isEmpty()) {
            throw new IllegalStateException("Finalize a sessão anterior antes de iniciar outra.");
        }
        
        reuniao.setStatus(StatusReuniao.EM_ANDAMENTO);

        return reuniaoRepository.save(reuniao);
    }

    @Transactional
    public void removerProcessoDaPauta(Long reuniaoId, Long processoId) {
        Reuniao reuniao = buscarPorIdComPauta(reuniaoId);
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado"));
        
        if (reuniao.getStatus() == StatusReuniao.ENCERRADA) {
            throw new RuntimeException("Não é possível remover processos de sessões encerradas.");
        }
        
        reuniao.getPauta().remove(processo);
        processo.setReuniao(null);
        processo.setStatus(StatusProcessoEnum.DISTRIBUIDO);
        
        processoRepository.save(processo);
        reuniaoRepository.save(reuniao);
    }

    @Transactional
    public Processo iniciarJulgamentoProcesso(Long reuniaoId, Long processoId) {
        Reuniao reuniao = buscarPorId(reuniaoId);
        
        if (reuniao.getStatus() != StatusReuniao.EM_ANDAMENTO) {
            throw new RuntimeException("A sessão precisa estar em andamento para julgar processos.");
        }
        
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado"));
        
        if (!reuniao.getPauta().contains(processo)) {
            throw new RuntimeException("Processo não está na pauta desta sessão.");
        }
        
        processo.setStatus(StatusProcessoEnum.EM_JULGAMENTO);
        return processoRepository.save(processo);
    }
}
