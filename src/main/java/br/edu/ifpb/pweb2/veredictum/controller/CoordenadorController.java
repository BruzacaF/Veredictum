package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTOFiltro;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Reuniao;
import br.edu.ifpb.pweb2.veredictum.repository.AssuntoRepository;
import br.edu.ifpb.pweb2.veredictum.repository.UsuarioRepository;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.ProcessoService;
import br.edu.ifpb.pweb2.veredictum.service.ProfessorService;
import br.edu.ifpb.pweb2.veredictum.service.ReuniaoService;
import br.edu.ifpb.pweb2.veredictum.service.ColegiadoService;
import br.edu.ifpb.pweb2.veredictum.model.Colegiado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

@Controller
@RequestMapping("/coordenador")
public class CoordenadorController {

    @Autowired
    private ProcessoService processoService;

    @Autowired
    private AssuntoRepository assuntoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private ReuniaoService reuniaoService;

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
        long totalRealizadas = sessoes.stream()
                .filter(r -> r.getStatus() == StatusReuniao.REALIZADA)
                .count();
        long totalCanceladas = sessoes.stream()
                .filter(r -> r.getStatus() == StatusReuniao.CANCELADA)
                .count();

        List<Processo> processosDisponiveis = processoService.listarProcessosDisponiveisParaPauta(colegiadoId);
        List<Colegiado> colegiados = colegiadoService.listarTodos();

        model.addAttribute("sessoes", sessoes);
        model.addAttribute("totalProgramadas", totalProgramadas);
        model.addAttribute("totalRealizadas", totalRealizadas);
        model.addAttribute("totalCanceladas", totalCanceladas);
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
