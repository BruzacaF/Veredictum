package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import br.edu.ifpb.pweb2.veredictum.enums.TipoDecisao;
import br.edu.ifpb.pweb2.veredictum.enums.TipoVoto;
import br.edu.ifpb.pweb2.veredictum.model.*;
import br.edu.ifpb.pweb2.veredictum.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReuniaoService {

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    @Autowired
    private ColegiadoRepository colegiadoRepository;

    @Autowired
    private ProcessoRepository processoRepository;
    
    @Autowired
    private VotoRepository votoRepository;
    
    @Autowired
    private ProfessorRepository professorRepository;

    public List<Reuniao> buscarPorProfessor(Professor professor) {
        return reuniaoRepository.findByColegiado_Membros(professor);
    }

    public List<Reuniao> buscarReuniosProfessorFiltro(Professor professor, StatusReuniao status, LocalDate data) {
        List<Reuniao> reunioes = buscarPorProfessor(professor);
        
        // Aplicar filtro de status se fornecido
        if (status != null) {
            reunioes = reunioes.stream()
                    .filter(r -> r.getStatus() == status)
                    .collect(Collectors.toList());
        }
        
        // Aplicar filtro de data se fornecido
        if (data != null) {
            reunioes = reunioes.stream()
                    .filter(r -> r.getData().toLocalDate().equals(data))
                    .collect(Collectors.toList());
        }
        
        // Ordenar por data decrescente
        reunioes.sort((r1, r2) -> r2.getData().compareTo(r1.getData()));
        
        return reunioes;
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

    @Transactional
    public void registrarVoto(Long reuniaoId, Long processoId, Professor professor, String decisao, boolean ehCoordenador) {
        // Buscar processo
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado"));
        
        // Verificar se já existe voto deste professor para este processo
        Optional<Voto> votoExistente = votoRepository.findByProfessorAndProcesso(professor, processo);
        if (votoExistente.isPresent()) {
            throw new RuntimeException("Você já votou neste processo.");
        }

        TipoVoto tipoVoto = converterDecisaoParaVoto(decisao, processo, ehCoordenador);

        // Criar e salvar o voto
        Voto voto = new Voto();
        voto.setProfessor(professor);
        voto.setProcesso(processo);
        voto.setVoto(tipoVoto);
        voto.setDataVoto(LocalDateTime.now());

        votoRepository.save(voto);
    }
    
    private TipoVoto converterDecisaoParaVoto(String decisao, Processo processo, boolean ehCoordenador) {
        String decisaoRecebida = decisao.toUpperCase();

        // Para coordenadores: decisões diretas (DEFERIDO, INDEFERIDO, AUSENTE)
        // Para membros: COM_RELATOR ou DIVERGENTE (baseado na decisão do relator)
        if (decisaoRecebida.equals("COM_RELATOR")) {
            // Votar igual ao relator
            if (processo.getDecisaoRelator() == null) {
                throw new RuntimeException("Não é possível votar pois o relator ainda não decidiu.");
            }
            // Converter TipoDecisao para TipoVoto
            return processo.getDecisaoRelator() == TipoDecisao.DEFERIMENTO ? 
                   TipoVoto.DEFERIDO : TipoVoto.INDEFERIDO;
            
        } else if (decisaoRecebida.equals("DIVERGENTE")) {
            // Votar contrário ao relator
            if (processo.getDecisaoRelator() == null) {
                throw new RuntimeException("Não é possível votar pois o relator ainda não decidiu.");
            }
            // Inverter a decisão do relator e converter para TipoVoto
            return processo.getDecisaoRelator() == TipoDecisao.DEFERIMENTO ? 
                   TipoVoto.INDEFERIDO : TipoVoto.DEFERIDO;
                   
        } else if (decisaoRecebida.equals("AUSENTE")) {
            return TipoVoto.AUSENTE;
            
        } else {
            // Para decisões diretas (coordenador)
            try {
                return TipoVoto.valueOf(decisaoRecebida);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Tipo de voto inválido: " + decisao);
            }
        }
    }

    public void encerrarSessao(Long reuniaoId) {

        Reuniao reuniao = buscarPorId(reuniaoId);
        reuniao.setStatus(StatusReuniao.ENCERRADA);
        reuniaoRepository.save(reuniao);
    }
}

