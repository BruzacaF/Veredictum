package br.edu.ifpb.pweb2.veredictum.controller.controllerAdvice;

import br.edu.ifpb.pweb2.veredictum.controller.HomeController;
import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {HomeController.class})
public class ProcessoFormAdvice {

    @ModelAttribute("processoDTO")
    public ProcessoDTO processoDTO() {
        return new ProcessoDTO();
    }
}
