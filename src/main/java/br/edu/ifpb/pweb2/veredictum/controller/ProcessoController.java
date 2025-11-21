package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTO;
import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTOFiltro;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/processo")
class ProcessoController {

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
                                    @AuthenticationPrincipal UsuarioDetails usuarioDetails) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Preencha todos os campos necessarios");
            return "redirect:/home";
        }

            try {
                Processo processoCriado = processoService.criar(processo, usuarioDetails.getUsuario());
                redirectAttributes.addFlashAttribute("success", "Processo número "+ processoCriado.getNumeroProcesso() + " adicionado com sucesso e está em" + processoCriado.getStatus());

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
            return "redirect:/home";
        };

    @GetMapping("/listar")
    public String listar(
            @RequestParam(required = false) String assunto,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "DESC" )String ordencao,
            Model model,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails,
            @RequestHeader(value = "X-requested-Width", required = false) String requestedWidth
    ) {

        Map<String, Object> params = new HashMap<>();
        params.put("assunto", assunto);
        params.put("status", status);
        params.put("ordencao", ordencao);

        ProcessoDTOFiltro filtro = new ProcessoDTOFiltro();
        filtro.setAssunto(assunto);
        filtro.setStatus(status);
        filtro.setOrdenacao(ordencao);

        Usuario usuario = usuarioDetails.getUsuario();
        Long alunoId = usuarioDetails.getUsuario().getId();

        List<Processo> processos = processoRepository.filtrar(filtro, alunoId);
        model.addAttribute("assuntos", assuntoRepository.findAll());
        model.addAttribute("usuario", usuario);

        model.addAttribute("processos", processos);
        model.addAttribute("listaStatus", StatusProcessoEnum.values());
        model.addAttribute(
                "param", params
        );

        if ("XMLHttpRequest".equalsIgnoreCase(requestedWidth)) {
            return "fragments/tabela-processo :: tabela-processo" ;

        }
        return "fragments/tabela-processo :: tabela-processo";
    }




}



