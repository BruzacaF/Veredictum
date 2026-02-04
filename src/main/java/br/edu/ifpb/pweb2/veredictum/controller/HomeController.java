package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import br.edu.ifpb.pweb2.veredictum.enums.TipoDecisao;
import br.edu.ifpb.pweb2.veredictum.model.Aluno;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Reuniao;
import br.edu.ifpb.pweb2.veredictum.repository.AssuntoRepository;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.ProcessoService;
import br.edu.ifpb.pweb2.veredictum.service.ReuniaoService;
import br.edu.ifpb.pweb2.veredictum.service.VotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller

@RequestMapping("/home")
public class HomeController {
    @Autowired
    private ProcessoService processoService;
    @Autowired
    private AssuntoRepository assuntoRepository;
    @Autowired
    private ReuniaoService reuniaoService;
    @Autowired
    private VotoService votoService;

    @GetMapping("/aluno")
    public String home(Model model, @AuthenticationPrincipal UsuarioDetails usuario) {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Processo> processosPage = processoService.buscarPorAlunoPaginado(
                (Aluno) usuario.getUsuario(),
                pageable
        );

        model.addAttribute("itensTabela", processosPage.getContent());
        model.addAttribute("paginaAtual", processosPage.getNumber());
        model.addAttribute("totalPaginas", processosPage.getTotalPages());

        return "aluno/dashboard";
    }


    @GetMapping("/professor")
    public String professor(Model model,
                            @AuthenticationPrincipal UsuarioDetails usuario) {

        Professor professor = (Professor) usuario.getUsuario();
        Pageable pageable = PageRequest.of(0, 5);

        Page<Processo> processosPage = processoService.buscarPorRelatorPaginado(
                professor,
                pageable
        );

        List<Reuniao> reuniaoList = reuniaoService.buscarPorProfessor(professor);
        
        model.addAttribute("itensTabela", processosPage.getContent());
        model.addAttribute("reunioes", reuniaoList);
        model.addAttribute("status", StatusReuniao.values());
        model.addAttribute("paginaAtual", processosPage.getNumber());
        model.addAttribute("totalPaginas", processosPage.getTotalPages());
        model.addAttribute("usuario", professor);

        return "professor/dashboard";
    }

    @PostMapping("/professor/processo/{processoId}/votar")
    public String votarProcesso(
            @PathVariable Long processoId,
            @RequestParam TipoDecisao tipoDecisao,
            @RequestParam(required = false) String justificativa,
            @AuthenticationPrincipal UsuarioDetails usuario,
            RedirectAttributes redirectAttributes) {
        
        try {
            Professor professor = (Professor) usuario.getUsuario();
            Processo processo = processoService.buscarPorId(processoId);
            
            if (processo == null) {
                redirectAttributes.addFlashAttribute("error", "Processo não encontrado");
                return "redirect:/home/professor";
            }
            
            votoService.registrarVoto(processo, professor, tipoDecisao, justificativa);
            
            String mensagem = tipoDecisao == TipoDecisao.DEFERIMENTO 
                ? "Voto de DEFERIMENTO registrado com sucesso!" 
                : "Voto de INDEFERIMENTO registrado com sucesso!";
            
            redirectAttributes.addFlashAttribute("success", mensagem);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao registrar voto: " + e.getMessage());
        }
        
        return "redirect:/home/professor";
    }

}
