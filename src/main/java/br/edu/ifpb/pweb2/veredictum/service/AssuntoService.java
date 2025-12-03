package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.model.Assunto;
import br.edu.ifpb.pweb2.veredictum.repository.AssuntoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssuntoService {

    @Autowired
    private AssuntoRepository assuntoRepository;

    public List<Assunto> listarTodos() {
        return assuntoRepository.findAll();
    }

    public Assunto buscarPorId(Long id) {
        return assuntoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assunto não encontrado"));
    }

    @Transactional
    public Assunto salvar(Assunto assunto) {
        return assuntoRepository.save(assunto);
    }

    @Transactional
    public Assunto atualizar(Assunto assunto) {
        if (assunto.getId() == null) {
            throw new RuntimeException("ID do assunto não pode ser nulo para atualização");
        }
        buscarPorId(assunto.getId()); // Verifica se existe
        return assuntoRepository.save(assunto);
    }

    @Transactional
    public void excluir(Long id) {
        Assunto assunto = buscarPorId(id);
        if (!assunto.getProcessos().isEmpty()) {
            throw new RuntimeException("Não é possível excluir assunto com processos vinculados");
        }
        assuntoRepository.deleteById(id);
    }

    public long contarAssuntos() {
        return assuntoRepository.count();
    }
}
