package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import br.edu.ifpb.pweb2.veredictum.repository.ProcessoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessoService {
    @Autowired
    ProcessoRepository processoRepository;
    @Autowired
    private UsuarioService usuarioService;

    public Processo criar(Processo processo, Usuario aluno) {
        processo.setAluno(aluno);
        processoRepository.save(processo);
        return processoRepository.save(processo);
    }

    public List<Processo> buscarPorAluno(Usuario aluno) {
        return processoRepository.findByAluno(aluno);
    }
}
