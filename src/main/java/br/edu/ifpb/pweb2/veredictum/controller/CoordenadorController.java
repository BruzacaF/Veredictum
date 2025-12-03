package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTOFiltro;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.repository.AssuntoRepository;
import br.edu.ifpb.pweb2.veredictum.repository.UsuarioRepository;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.ProcessoService;
import br.edu.ifpb.pweb2.veredictum.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
