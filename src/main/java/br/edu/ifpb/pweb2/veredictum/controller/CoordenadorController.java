package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTOFiltro;
import br.edu.ifpb.pweb2.veredictum.dto.VotoMembroDTO;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import br.edu.ifpb.pweb2.veredictum.enums.TipoDecisao;
import br.edu.ifpb.pweb2.veredictum.enums.TipoVoto;
import br.edu.ifpb.pweb2.veredictum.model.*;
import br.edu.ifpb.pweb2.veredictum.repository.*;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.ColegiadoService;
import br.edu.ifpb.pweb2.veredictum.service.ProcessoService;
import br.edu.ifpb.pweb2.veredictum.service.ProfessorService;
import br.edu.ifpb.pweb2.veredictum.service.ReuniaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/coordenador")
public class CoordenadorController {

    @Autowired
    private ProcessoService processoService;

    @Autowired
    private ProcessoRepository processoRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private AssuntoRepository assuntoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private ReuniaoService reuniaoService;

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    @Autowired
    private ColegiadoService colegiadoService;

    @GetMapping("/processos")
    @Transactional(readOnly = true)
    public String listarProcessos(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nomeAluno,
            @RequestParam(required = false) String nomeRelator,
            @RequestParam(required = false, defaultValue = "DESC") String ordenacao,
            Model model,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith
    ) {

        Long coordenadorId = usuarioDetails.getUsuario().getId();
        Professor coordenador = (Professor) usuarioRepository.findById(coordenadorId)
                .orElseThrow(() -> new RuntimeException("Coordenador não encontrado"));

        if (!coordenador.isEhCoordenador()) {
            model.addAttribute("error", "Acesso negado. Apenas coordenadores podem acessar esta funcionalidade.");
            return "error/403";
        }

        Long colegiadoId = coordenador.getColegiados().stream()
                .findFirst()
                .map(colegiado -> colegiado.getId())
                .orElseThrow(() -> new RuntimeException("Coordenador não possui colegiado associado"));

        ProcessoDTOFiltro filtro = new ProcessoDTOFiltro();
        filtro.setStatus(status);
        filtro.setNomeAluno(nomeAluno);
        filtro.setNomeRelator(nomeRelator);
        filtro.setOrdenacao(ordenacao);
        filtro.setColegiadoId(colegiadoId);

        List<Processo> processos = processoService.listarProcessosDoColegiado(filtro, colegiadoId);

        long totalEmAnalise = processos.stream()
                .filter(p -> p.getStatus() == StatusProcessoEnum.DISTRIBUIDO ||
                        p.getStatus() == StatusProcessoEnum.EM_PAUTA)
                .count();

        model.addAttribute("processos", processos);
        model.addAttribute("totalEmAnalise", totalEmAnalise);
        model.addAttribute("totalProcessos", processos.size());
        model.addAttribute("assuntos", assuntoRepository.findAll());
        model.addAttribute("listaStatus", StatusProcessoEnum.values());
        model.addAttribute("usuario", coordenador);
        model.addAttribute("professores", professorService.buscarPorColegiadoId(colegiadoId));

        if ("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
            return "fragments/tabela-processo :: tabela-processo";
        }

        return "coordenador/processos";
    }

    @GetMapping("/sessoes")
    @Transactional(readOnly = true)
    public String listarSessoes(
            Model model,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {
        Professor coordenador = obterCoordenador(usuarioDetails);
        Long colegiadoId = obterColegiadoId(coordenador);

        List<Reuniao> sessoes = reuniaoService.listarPorCoordenador(coordenador);
        
        long totalProgramadas = sessoes.stream()
                .filter(r -> r.getStatus() == StatusReuniao.PROGRAMADA)
                .count();
        long totalEncerradas = sessoes.stream()
                .filter(r -> r.getStatus() == StatusReuniao.ENCERRADA)
                .count();
        long totalEmAndamento = sessoes.stream()
                .filter(r -> r.getStatus() == StatusReuniao.EM_ANDAMENTO)
                .count();

        List<Processo> processosDisponiveis = processoService.listarProcessosDisponiveisParaPauta(colegiadoId);
        List<Colegiado> colegiados = colegiadoService.listarTodos();

        model.addAttribute("sessoes", sessoes);
        model.addAttribute("totalProgramadas", totalProgramadas);
        model.addAttribute("totalEncerradas", totalEncerradas);
        model.addAttribute("totalEmAndamento", totalEmAndamento);
        model.addAttribute("processosDisponiveis", processosDisponiveis);
        model.addAttribute("colegiados", colegiados);
        model.addAttribute("usuario", coordenador);

        return "coordenador/sessoes";
    }

    @GetMapping("/api/colegiado/{id}/professores")
    @Transactional(readOnly = true)
    public String buscarProfessoresColegiado(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails,
            Model model
    ) {
        try {
            obterCoordenador(usuarioDetails);
            List<Professor> professores = professorService.buscarPorColegiadoId(id);
            model.addAttribute("professores", professores);
            return "fragments/membros-colegiado :: lista-membros";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "fragments/membros-colegiado :: erro";
        }
    }

    @PostMapping("/sessao/criar")
    @Transactional
    public String criarSessao(
            @RequestParam Long colegiadoId,
            @RequestParam String data,
            @RequestParam String hora,
            @RequestParam(required = false) List<Long> processosIds,
            @RequestParam(required = false) List<Long> professoresIds,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Professor coordenador = obterCoordenador(usuarioDetails);

            LocalDateTime dataHora = LocalDateTime.of(
                    LocalDate.parse(data),
                    LocalTime.parse(hora)
            );

            Reuniao reuniao = reuniaoService.criarSessao(colegiadoId, dataHora, processosIds, professoresIds);

            String dataFormatada = dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH'h'mm"));
            int qtdProcessos = processosIds != null ? processosIds.size() : 0;
            
            redirectAttributes.addFlashAttribute("success",
                    String.format("✅ Sessão agendada para %s com %d processo(s) em pauta.",
                            dataFormatada, qtdProcessos));

            return "redirect:/coordenador/sessoes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao criar sessão: " + e.getMessage());
            return "redirect:/coordenador/sessoes";
        }
    }

    @GetMapping("/sessao/{id}")
    @Transactional(readOnly = true)
    public String visualizarSessao(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails,
            Model model
    ) {
        try {
            Professor coordenador = obterCoordenador(usuarioDetails);
            Reuniao sessao = reuniaoService.buscarPorIdComPauta(id);
            
            if (sessao == null) {
                throw new RuntimeException("Sessão não encontrada");
            }
            
            // Verificar se o coordenador tem acesso a esta sessão
            if (!sessao.getCoordenador().getId().equals(coordenador.getId())) {
                throw new RuntimeException("Acesso negado a esta sessão");
            }
            
            model.addAttribute("sessao", sessao);
            model.addAttribute("usuario", coordenador);
            model.addAttribute("ehCoordenador", true);
            
            return "reuniao/detalhes-sessao";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/coordenador/sessoes";
        }
    }

    @PostMapping("/sessao/{id}/excluir")
    @Transactional
    public String excluirSessao(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Professor coordenador = obterCoordenador(usuarioDetails);
            Reuniao sessao = reuniaoService.buscarPorIdComPauta(id);
            
            if (sessao == null) {
                throw new RuntimeException("Sessão não encontrada");
            }
            
            // Verificar se o coordenador tem permissão para excluir esta sessão
            if (!sessao.getCoordenador().getId().equals(coordenador.getId())) {
                throw new RuntimeException("Acesso negado. Apenas o coordenador responsável pode excluir esta sessão.");
            }
            
            // Não permitir excluir sessões já encerrdas
            if (sessao.getStatus() == StatusReuniao.ENCERRADA) {
                throw new RuntimeException("Não é possível excluir sessões já encerradas.");
            }
            
            reuniaoService.excluirSessao(id);
            
            redirectAttributes.addFlashAttribute("success", "✅ Sessão excluída com sucesso!");
            return "redirect:/coordenador/sessoes";
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "❌ Erro ao excluir sessão: " + e.getMessage());
            return "redirect:/coordenador/sessoes";
        }
    }

    @PostMapping("/sessao/{reuniaoId}/processo/{processoId}/remover")
    @Transactional
    public String removerProcessoDaPauta(
            @PathVariable Long reuniaoId,
            @PathVariable Long processoId,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Professor coordenador = obterCoordenador(usuarioDetails);
            Reuniao sessao = reuniaoService.buscarPorId(reuniaoId);
            
            if (!sessao.getCoordenador().getId().equals(coordenador.getId())) {
                throw new RuntimeException("Acesso negado.");
            }
            
            reuniaoService.removerProcessoDaPauta(reuniaoId, processoId);
            redirectAttributes.addFlashAttribute("success", "✅ Processo removido da pauta!");
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "❌ " + e.getMessage());
        }
        
        return "redirect:/coordenador/sessao/" + reuniaoId;
    }

    @PostMapping("/sessao/{reuniaoId}/processo/{processoId}/julgar")
    @Transactional
    public String iniciarJulgamentoProcesso(
            @PathVariable Long reuniaoId,
            @PathVariable Long processoId,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Professor coordenador = obterCoordenador(usuarioDetails);
            Reuniao sessao = reuniaoService.buscarPorId(reuniaoId);
            
            if (!sessao.getCoordenador().getId().equals(coordenador.getId())) {
                throw new RuntimeException("Acesso negado.");
            }
            
            reuniaoService.iniciarJulgamentoProcesso(reuniaoId, processoId);
            return "redirect:/coordenador/sessao/" + reuniaoId + "/processo/" + processoId + "/julgamento";
            
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/coordenador/sessao/" + reuniaoId;
        }
    }

    @PostMapping("/sessao/{sessaoId}/processo/{processoId}/concluir")
    @Transactional
    public String concluirJulgamento(@PathVariable Long sessaoId,
                                     @PathVariable Long processoId,
                                     @AuthenticationPrincipal UsuarioDetails usuarioDetails,
                                     RedirectAttributes redirectAttributes) {

        try {
            Professor coordenador = obterCoordenador(usuarioDetails);
            Reuniao sessao = reuniaoRepository.findById(sessaoId)
                    .orElseThrow(() -> new RuntimeException("Sessão não encontrada"));
            
            // Validar que é o coordenador da sessão
            if (!sessao.getCoordenador().getId().equals(coordenador.getId())) {
                redirectAttributes.addFlashAttribute("error", "Apenas o coordenador pode concluir julgamentos");
                return "redirect:/coordenador/sessao/" + sessaoId;
            }
            
            Optional<Processo> processoOpt = processoRepository.findById(processoId);
            if (processoOpt.isPresent()) {
                Processo processo = processoOpt.get();

                // Marcar membros sem voto como AUSENTE
                for (Usuario membro : sessao.getMembros()) {
                    boolean jaVotou = processo.getVotos().stream()
                            .anyMatch(v -> v.getProfessor().getId().equals(membro.getId()));
                    if (!jaVotou) {
                        Voto voto = new Voto();
                        voto.setProfessor((Professor) membro);
                        voto.setProcesso(processo);
                        voto.setVoto(TipoVoto.AUSENTE);
                        voto.setDataVoto(LocalDateTime.now());
                        votoRepository.save(voto);
                    }
                }

                // Calcular resultado final baseado nos votos
                List<Voto> votos = votoRepository.findByProcessoId(processoId);
                
                long votosDeferidos = votos.stream()
                        .filter(v -> v.getVoto() == TipoVoto.DEFERIDO)
                        .count();
                
                long votosIndeferidos = votos.stream()
                        .filter(v -> v.getVoto() == TipoVoto.INDEFERIDO)
                        .count();

                // Determinar resultado final
                TipoDecisao decisaoFinal;
                if (votosDeferidos > votosIndeferidos) {
                    decisaoFinal = TipoDecisao.DEFERIMENTO;
                } else if (votosIndeferidos > votosDeferidos) {
                    decisaoFinal = TipoDecisao.INDEFERIMENTO;
                } else {
                    // Empate: voto de minerva do coordenador
                    Voto votoCoordenador = votos.stream()
                            .filter(v -> v.getProfessor().getId().equals(coordenador.getId()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Voto do coordenador não encontrado"));
                    
                    decisaoFinal = votoCoordenador.getVoto() == TipoVoto.DEFERIDO ? 
                                  TipoDecisao.DEFERIMENTO : TipoDecisao.INDEFERIMENTO;
                }

                // Atualizar processo com decisão final
                processo.setDecisaoFinal(decisaoFinal);
                processo.setStatus(StatusProcessoEnum.JULGADO);
                processoRepository.save(processo);

                redirectAttributes.addFlashAttribute("success", "✅ Julgamento concluído! Resultado: " + decisaoFinal.name());
            } else {
                redirectAttributes.addFlashAttribute("error", "Processo não encontrado.");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "❌ Erro: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Erro inesperado ao concluir julgamento");
        }

        return "redirect:/coordenador/sessao/" + sessaoId + "/processo/" + processoId + "/julgamento";
    }


    @GetMapping("/sessao/{sessaoId}/processo/{processoId}/julgamento")
    public String julgamentoProcesso(@PathVariable Long sessaoId,
                                     @PathVariable Long processoId,
                                     Model model,
                                     @AuthenticationPrincipal UsuarioDetails usuario) {

        System.out.println("============================chegour aqui=============================");

        Reuniao sessao = reuniaoService.buscarPorId(sessaoId);
        Processo processo = processoService.buscarPorId(processoId);

        // Mapear votos por membro
        List<VotoMembroDTO> votosMembros = sessao.getMembros().stream()
                .map(membro -> {
                    Voto voto = processo.getVotos().stream()
                            .filter(v -> v.getProfessor() != null && v.getProfessor().getId().equals(membro.getId()))
                            .findFirst()
                            .orElse(null);
                    return new VotoMembroDTO(membro, voto);
                })
                .toList();

        model.addAttribute("podeVotar", votosMembros.stream()
                .anyMatch(v -> v.getMembro().getId().equals(usuario.getUsuario().getId()) && v.getVoto() == null
                        && processo.getStatus() == StatusProcessoEnum.EM_JULGAMENTO));


        // Flag para saber se é coordenador
        boolean ehCoordenador = usuario.getUsuario().getId().equals(sessao.getCoordenador().getId());

        model.addAttribute("sessao", sessao);
        model.addAttribute("processo", processo);
        model.addAttribute("usuario", usuario.getUsuario());
        model.addAttribute("ehCoordenador", ehCoordenador);
        model.addAttribute("votosMembros", votosMembros);

        System.out.println("votosMembros: " + votosMembros);

        return "reuniao/julgamento-processo";
    }


    private Professor obterCoordenador(UsuarioDetails usuarioDetails) {
        Long coordenadorId = usuarioDetails.getUsuario().getId();
        Professor coordenador = (Professor) usuarioRepository.findById(coordenadorId)
                .orElseThrow(() -> new RuntimeException("Coordenador não encontrado"));
        
        if (!coordenador.isEhCoordenador()) {
            throw new RuntimeException("Acesso negado. Apenas coordenadores podem acessar esta funcionalidade.");
        }
        return coordenador;
    }

    private Long obterColegiadoId(Professor coordenador) {
        return coordenador.getColegiados().stream()
                .findFirst()
                .map(colegiado -> colegiado.getId())
                .orElseThrow(() -> new RuntimeException("Coordenador não possui colegiado associado"));
    }
}
