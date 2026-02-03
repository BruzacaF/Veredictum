package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import br.edu.ifpb.pweb2.veredictum.enums.TipoVoto;
import br.edu.ifpb.pweb2.veredictum.model.*;
import br.edu.ifpb.pweb2.veredictum.repository.ProcessoRepository;
import br.edu.ifpb.pweb2.veredictum.repository.VotoRepository;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.ReuniaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reuniao")
class ReuniaoController {

    @Autowired
    private final ProcessoRepository  processoRepository;
    @Autowired
    private final VotoRepository votoRepository;

    private static final Logger logger = LoggerFactory.getLogger(ReuniaoController.class);

    @Autowired
    private final ReuniaoService reuniaoService;


    ReuniaoController(ProcessoRepository processoRepository, VotoRepository votoRepository, ReuniaoService reuniaoService) {
        this.processoRepository = processoRepository;
        this.votoRepository = votoRepository;
        this.reuniaoService = reuniaoService;
    }

    @GetMapping("/professor/listar")
    @Transactional(readOnly = true)
    public String listarReuniaoProfessor(
            @AuthenticationPrincipal UsuarioDetails usuario,
            @RequestParam(required = false) StatusReuniao status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            Model model
    ) {
        try {
            logger.info("Listando reuniões - Status: {}, Data: {}", status, data);

            Professor professor = (Professor) usuario.getUsuario();
            List<Reuniao> reunioes = reuniaoService.buscarReuniosProfessorFiltro(professor, status, data);

            logger.info("Encontradas {} reuniões", reunioes.size());

            model.addAttribute("reunioes", reunioes);
            model.addAttribute("status", StatusReuniao.values());
            model.addAttribute("dataFiltro", data);
            model.addAttribute("statusFiltro", status);

            return "fragments/painel-reuniao :: painel-reuniao";
        } catch (Exception e) {
            logger.error("Erro ao carregar reuniões", e);
            model.addAttribute("reunioes", List.of());
            model.addAttribute("status", StatusReuniao.values());
            model.addAttribute("error", "Erro ao carregar reuniões: " + e.getMessage());
            return "fragments/painel-reuniao :: painel-reuniao";
        }
    }

    @GetMapping("/{id}/modal")
    @Transactional(readOnly = true)
    public String detalhesModal(@PathVariable Long id, Model model) {
        try {
            logger.info("Carregando detalhes da reunião: {}", id);
            Reuniao reuniao = reuniaoService.buscarPorIdComPauta(id);
            if (reuniao == null) {
                throw new RuntimeException("Reunião não encontrada");
            }
            model.addAttribute("reuniao", reuniao);
            return "fragments/modal-reuniao :: conteudo";
        } catch (Exception e) {
            logger.error("Erro ao carregar modal da reunião: {}", id, e);
            model.addAttribute("error", e.getMessage());
            return "fragments/modal-reuniao :: erro";
        }
    }

    @PostMapping("/{id}/iniciar")
    @Transactional
    public String iniciarSessao(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails usuario,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Professor coordenador = (Professor) usuario.getUsuario();
            Reuniao sessao = reuniaoService.buscarPorId(id);

            if (sessao == null) {
                throw new RuntimeException("Sessão não encontrada");
            }

            if (!sessao.getCoordenador().getId().equals(coordenador.getId())) {
                throw new RuntimeException("Acesso negado. Apenas o coordenador responsável pode iniciar esta sessão.");
            }

            if (sessao.getStatus() != StatusReuniao.PROGRAMADA) {
                throw new RuntimeException("Apenas sessões programadas podem ser iniciadas.");
            }

            reuniaoService.iniciarSessao(id);
            redirectAttributes.addFlashAttribute("success", "✅ Sessão iniciada com sucesso!");
            return "redirect:/coordenador/sessao/" + id;

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "⚠️ " + e.getMessage());
            return "redirect:/coordenador/sessoes";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "❌ Erro ao iniciar sessão: " + e.getMessage());
            return "redirect:/coordenador/sessoes";
        }
    }

    @GetMapping("/{id}/detalhes")
    @Transactional(readOnly = true)
    public String detalhesReuniao(
            @PathVariable Long id,
            @AuthenticationPrincipal UsuarioDetails usuario,
            Model model
    ) {
        try {
            logger.info("Visualizando detalhes da reunião: {}", id);

            Professor professor = (Professor) usuario.getUsuario();
            Reuniao sessao = reuniaoService.buscarPorIdComPauta(id);

            if (sessao == null) {
                throw new RuntimeException("Reunião não encontrada");
            }

            // Verificar se o professor faz parte desta reunião
            boolean fazParte = sessao.getMembros().stream()
                    .anyMatch(m -> m.getId().equals(professor.getId()));

            if (!fazParte && !sessao.getCoordenador().getId().equals(professor.getId())) {
                throw new RuntimeException("Você não tem acesso a esta reunião");
            }

            model.addAttribute("sessao", sessao);
            model.addAttribute("usuario", professor);
            model.addAttribute("ehCoordenador", professor.isEhCoordenador());

            return "reuniao/detalhes-sessao";

        } catch (RuntimeException e) {
            logger.error("Erro ao visualizar reunião: {}", id, e);
            model.addAttribute("error", e.getMessage());
            return "redirect:/home";
        }
    }

    @GetMapping("/{reuniaoId}/processo/{processoId}/visualizar")
    @Transactional(readOnly = true)
    public String visualizarJulgamento(
            @PathVariable Long reuniaoId,
            @PathVariable Long processoId,
            @AuthenticationPrincipal UsuarioDetails usuario,
            Model model
    ) {
        try {
            logger.info("Professor visualizando julgamento - Reunião: {}, Processo: {}", reuniaoId, processoId);

            Professor professor = (Professor) usuario.getUsuario();
            Reuniao sessao = reuniaoService.buscarPorIdComPauta(reuniaoId);

            if (sessao == null) {
                throw new RuntimeException("Reunião não encontrada");
            }

            // Verificar se o professor faz parte desta reunião
            boolean fazParte = sessao.getMembros().stream()
                    .anyMatch(m -> m.getId().equals(professor.getId()));

            if (!fazParte && !sessao.getCoordenador().getId().equals(professor.getId())) {
                throw new RuntimeException("Você não tem acesso a esta reunião");
            }

            Processo processo = sessao.getPauta().stream()
                    .filter(p -> p.getId().equals(processoId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Processo não encontrado na pauta desta reunião"));

            model.addAttribute("sessao", sessao);
            model.addAttribute("processo", processo);
            model.addAttribute("usuario", professor);
            model.addAttribute("ehCoordenador", professor.isEhCoordenador() &&
                    sessao.getCoordenador().getId().equals(professor.getId()));

            return "reuniao/julgamento-processo";

        } catch (RuntimeException e) {
            logger.error("Erro ao visualizar julgamento: Reunião {}, Processo {}", reuniaoId, processoId, e);
            model.addAttribute("error", e.getMessage());
            return "redirect:/home";
        }
    }

    @PostMapping("/{reuniaoId}/processo/{processoId}/votar")
    @Transactional
    public String registrarVoto(@PathVariable Long reuniaoId,
                                @PathVariable Long processoId,
                                @RequestParam String decisao,
                                @AuthenticationPrincipal UsuarioDetails usuario,
                                RedirectAttributes redirectAttributes) {

        Optional<Processo> processoOpt = processoRepository.findById(processoId);
        if (processoOpt.isPresent()) {
            Processo processo = processoOpt.get();

            Voto voto = new Voto();
            voto.setProfessor((Professor) usuario.getUsuario());
            voto.setProcesso(processo);
            voto.setVoto(TipoVoto.valueOf(decisao)); // COM_RELATOR, DIVERGENTE ou AUSENTE
            voto.setDataVoto(LocalDateTime.now());

            votoRepository.save(voto);

            redirectAttributes.addFlashAttribute("success", "Voto registrado com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Processo não encontrado.");
        }

        if(usuario.getUsuario().getRole() != RoleEnum.COORDENADOR){
            return "redirect:/home";
        }

        return "redirect:/coordenador/sessao/" + reuniaoId + "/processo/" + processoId + "/julgamento";
    }

}
