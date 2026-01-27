package br.edu.ifpb.pweb2.veredictum.controller;

import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTO;
import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTOFiltro;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
            return "aluno/dashboard";
        }

            try {
                Processo processoCriado = processoService.criar(processo, (Aluno) usuarioDetails.getUsuario());
                redirectAttributes.addFlashAttribute("success", "Processo número "+ processoCriado.getNumero() + " adicionado com sucesso e está em" + processoCriado.getStatus());

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
            return "redirect:/home/aluno";
        };

    //UPLOAD DE ARQUIVOS PARA PROCESSO ALUNO
    @PostMapping("/{id}/arquivo")
    public String uploadArquivo(@PathVariable Long id,
                                @RequestParam("arquivo") MultipartFile arquivo,
                                RedirectAttributes redirectAttributes,
                                @AuthenticationPrincipal UsuarioDetails usuario) throws Exception {

        if (arquivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro",
                    "Nenhum arquivo foi selecionado.");
            return "redirect:/home/aluno";
        }

        processoService.anexarArquivo(id, arquivo, usuario.getUsuario());

        redirectAttributes.addFlashAttribute("sucesso",
                "Arquivo enviado com sucesso.");

        return "redirect:/home/aluno";
    }


    @GetMapping("/listar")
    public String listar(
            @RequestParam(required = false) String assunto,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "DESC") String ordenacao,
            @AuthenticationPrincipal UsuarioDetails usuarioDetails,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
            Model model
    ) {

        ProcessoDTOFiltro filtro = new ProcessoDTOFiltro();
        filtro.setAssunto(assunto);
        filtro.setStatus(status);
        filtro.setOrdenacao(ordenacao);

        Usuario usuario = usuarioDetails.getUsuario();

        List<Processo> processos = processoRepository.filtrar(
                filtro,
                usuario.getId(),
                usuario.getRole()
        );

        model.addAttribute("itensTabela", processos);

        if ("XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
            return "fragments/tabela-processo-aluno :: tabela-processo";
        }

        return "processo/listar";
    }


    @GetMapping("/{id}/modal")
    public String detalhesModal(@PathVariable Long id, Model model, @AuthenticationPrincipal UsuarioDetails usuario) {

        Processo processo = processoService.buscarPorId(id);

        int totalDocumentos = processo.getDocumentos().size();
        int limite = 3;

        boolean isAluno = usuario.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO"));


        boolean podeEnviarDocumento =
                isAluno &&
                processo.getStatus() == StatusProcessoEnum.CRIADO &&
                        totalDocumentos < limite;

        model.addAttribute("processo", processo);
        model.addAttribute("totalDocumentos", totalDocumentos);
        model.addAttribute("limiteDocumentos", limite);
        model.addAttribute("podeEnviarDocumento", podeEnviarDocumento);

        return "fragments/modal-processo :: conteudo";
    }



    @GetMapping("/professor/listarProcessos")
    public String listarRelator(
            @AuthenticationPrincipal UsuarioDetails usuarioDetails
    ) {

        processoService.buscarPorProfessorRelator((Professor) usuarioDetails.getUsuario());

        return "redirect:/professor/processoTabela";

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



