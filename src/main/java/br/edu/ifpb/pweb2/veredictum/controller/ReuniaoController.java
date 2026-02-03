package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import br.edu.ifpb.pweb2.veredictum.model.*;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.ReuniaoService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import java.util.List;

@Controller
@RequestMapping("/reuniao")
class ReuniaoController {

    private static final Logger logger = LoggerFactory.getLogger(ReuniaoController.class);

    @Autowired
    private final ReuniaoService reuniaoService;


    ReuniaoController(ReuniaoService reuniaoService) {
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

            // Preparar dados de votação
            List<java.util.Map<String, Object>> votosMembros = new java.util.ArrayList<>();
            for (Professor membro : sessao.getMembros()) {
                java.util.Map<String, Object> votoMembro = new java.util.HashMap<>();
                votoMembro.put("membro", membro);
                
                // Buscar voto do membro para este processo
                Voto voto = processo.getVotos().stream()
                        .filter(v -> v.getProfessor().getId().equals(membro.getId()))
                        .findFirst()
                        .orElse(null);
                votoMembro.put("voto", voto);
                
                votosMembros.add(votoMembro);
            }

            // Verificar se o professor pode votar (sessão em andamento e processo em julgamento)
            boolean podeVotar = sessao.getStatus() == StatusReuniao.EM_ANDAMENTO && 
                              processo.getStatus() == StatusProcessoEnum.EM_JULGAMENTO;

            model.addAttribute("sessao", sessao);
            model.addAttribute("processo", processo);
            model.addAttribute("usuario", professor);
            model.addAttribute("ehCoordenador", professor.isEhCoordenador() &&
                    sessao.getCoordenador().getId().equals(professor.getId()));
            model.addAttribute("votosMembros", votosMembros);
            model.addAttribute("podeVotar", podeVotar);

            return "reuniao/julgamento-processo";

        } catch (RuntimeException e) {
            logger.error("Erro ao visualizar julgamento: Reunião {}, Processo {}", reuniaoId, processoId, e);
            model.addAttribute("error", e.getMessage());
            return "redirect:/home";
        }
    }

    @PostMapping("/{reuniaoId}/processo/{processoId}/votar")
    public String registrarVoto(@PathVariable Long reuniaoId,
                                @PathVariable Long processoId,
                                @RequestParam String decisao,
                                @AuthenticationPrincipal UsuarioDetails usuario,
                                RedirectAttributes redirectAttributes) {

        try {
            Professor professor = (Professor) usuario.getUsuario();
            boolean ehCoordenador = usuario.getUsuario().getRole() == RoleEnum.COORDENADOR;
            
            // Delegar a lógica de negócio para o service
            reuniaoService.registrarVoto(reuniaoId, processoId, professor, decisao, ehCoordenador);
            
            redirectAttributes.addFlashAttribute("success", "Voto registrado com sucesso!");
            
        } catch (RuntimeException e) {
            logger.error("Erro ao registrar voto: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao registrar voto", e);
            redirectAttributes.addFlashAttribute("error", "Erro ao registrar voto: " + e.getMessage());
        }

        return redirecionarPorRole(usuario, reuniaoId, processoId);
    }

    private String redirecionarPorRole(UsuarioDetails usuario, Long reuniaoId, Long processoId) {
        if (usuario.getUsuario().getRole() == RoleEnum.COORDENADOR) {
            return "redirect:/coordenador/sessao/" + reuniaoId + "/processo/" + processoId + "/julgamento";
        }
        return "redirect:/reuniao/" + reuniaoId + "/processo/" + processoId + "/visualizar";
    }

}
