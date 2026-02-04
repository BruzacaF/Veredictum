package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.enums.TipoDecisao;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Voto;
import br.edu.ifpb.pweb2.veredictum.repository.VotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VotoService {
    
    @Autowired
    private VotoRepository votoRepository;

    @Transactional
    public Voto registrarVoto(Processo processo, Professor professor, 
                              TipoDecisao tipoDecisao, String justificativa) {
        
        // Verifica se já existe um voto do professor para este processo
        Optional<Voto> votoExistente = votoRepository.findByProfessorAndProcesso(professor, processo);
        
        Voto voto;
        if (votoExistente.isPresent()) {
            // Atualiza o voto existente
            voto = votoExistente.get();
            voto.setVoto(tipoDecisao);
            voto.setJustificativa(justificativa);
        } else {
            // Cria um novo voto
            voto = new Voto();
            voto.setProcesso(processo);
            voto.setProfessor(professor);
            voto.setVoto(tipoDecisao);
            voto.setJustificativa(justificativa);
        }
        
        return votoRepository.save(voto);
    }
    

    public boolean professorJaVotou(Long processoId, Long professorId) {
        return votoRepository.existsByProcessoIdAndProfessorId(processoId, professorId);
    }

    public Optional<Voto> buscarVotoDoProfessor(Professor professor, Processo processo) {
        return votoRepository.findByProfessorAndProcesso(professor, processo);
    }
}
