package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTO;
import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTOFiltro;
import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.model.Aluno;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import br.edu.ifpb.pweb2.veredictum.repository.AssuntoRepository;
import br.edu.ifpb.pweb2.veredictum.repository.ProcessoRepository;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import br.edu.ifpb.pweb2.veredictum.service.ProcessoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/processo")
public class ProcessoController {

    @Autowired
    ProcessoService processoService;
    @Autowired
    private ProcessoRepository processoRepository;
    @Autowired
    private AssuntoRepository assuntoRepository;

    @PostMapping("/adicionar")
    public String adicionarProcesso(@Valid @ModelAttribute ProcessoDTO processo,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    @AuthenticationPrincipal UsuarioDetails usuarioDetails,
                                    Model model) {
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()){
                System.out.println(error.getField() + ": " + error.getDefaultMessage());
                redirectAttributes.addFlashAttribute("error_" + error.getField(), error.getDefaultMessage());
            }
            redirectAttributes.addFlashAttribute("processoDTO", processo);
            return "redirect:/home/aluno";
        }

            try {
                Processo processoCriado = processoService.criar(processo, (Aluno) usuarioDetails.getUsuario());
                redirectAttributes.addFlashAttribute("success", "Processo número "+ processoCriado.getNumero() + " adicionado com sucesso e está em" + processoCriado.getStatus());

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
            return "redirect:/home/aluno";
        };

    @GetMapping("/listar")
    public String listar(
            @RequestParam(required = false) String assunto,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "DESC" )String ordenacao,
            Model model,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith
    ) {

        Map<String, Object> params = new HashMap<>();
        params.put("assunto", assunto);
        params.put("status", status);
        params.put("ordencao", ordenacao);

        ProcessoDTOFiltro filtro = new ProcessoDTOFiltro();
        filtro.setAssunto(assunto);
        filtro.setStatus(status);
        filtro.setOrdenacao(ordenacao);

        Usuario usuario = usuarioDetails.getUsuario();
        Long usuarioId = usuarioDetails.getUsuario().getId();
        RoleEnum role = usuario.getRole();

        System.out.println(filtro.getOrdenacao());

        List<Processo> processos = processoRepository.filtrar(filtro, usuarioId, role);
        model.addAttribute("assuntos", assuntoRepository.findAll());
        model.addAttribute("usuario", usuario);

        model.addAttribute("processos", processos);
        model.addAttribute("listaStatus", StatusProcessoEnum.values());
        model.addAttribute(
                "param", params
        );

        if ("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
            return "fragments/tabela-processo :: tabela-processo";
        }
        return "processo/listar";
    }


    @GetMapping("/professor/listarRelator")
    public String listarRelator(
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {

        processoService.buscarPorProfessorRelator((Professor) usuarioDetails.getUsuario());

        return "redirect:/home";

    }

    @PostMapping("/{id}/distribuir")
    public String distribuirProcesso(
            @PathVariable Long id,
            @RequestParam Long professorId,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails) {

        try {
            Processo processo = processoService.distribuirProcesso(id, professorId);

            redirectAttributes.addFlashAttribute("success",
                    String.format("Professor %s designado como relator do Processo nº %s.",
                            processo.getRelator().getNome(),
                            processo.getNumero()));

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/coordenador/processos";
    }

}



