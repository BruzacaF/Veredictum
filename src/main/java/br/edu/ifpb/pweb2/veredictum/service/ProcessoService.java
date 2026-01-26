package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTO;
import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTOFiltro;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.model.Aluno;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.repository.ProcessoRepository;
import br.edu.ifpb.pweb2.veredictum.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Service
public class ProcessoService {
    @Autowired
    ProcessoRepository processoRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ProfessorRepository professorRepository;

    @Value("${upload.dir}")
    private String uploadDir;

    public Processo criar(ProcessoDTO processoDTO, Aluno aluno) {
        try {

            Processo processo = new Processo();
            processo.setAssunto(processoDTO.getAssunto());
            processo.setTextoRequerimento(processoDTO.getTextoRequerimento());
            processo.setAluno(aluno);
            processo.setStatus(StatusProcessoEnum.CRIADO);
            processo.setDataCriacao(LocalDate.now());

            MultipartFile arquivo = processoDTO.getArquivo();
            
            String ano = String.valueOf(LocalDate.now().getYear());
            Long sequencial = processoRepository.count() + 1;
            processo.setNumero(ano + "-" + String.format("%03d", sequencial));

            if(arquivo != null && arquivo.isEmpty()){
                String nomeArquivo = UUID.randomUUID() + "_" +
                        arquivo.getOriginalFilename();
                Path destino = Paths.get(uploadDir, "processos", nomeArquivo);
                Files.createDirectories(destino.getParent());
                processoDTO.getArquivo().transferTo(destino.toFile());

                processo.setCaminhoArquivo(nomeArquivo);
            }

            return processoRepository.save(processo);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar processo" + e.getMessage(), e);
        }
    }

    public List<Processo> buscarPorAluno(Aluno aluno) {
        return processoRepository.findByAluno(aluno);
    }

    public List<Processo> buscarPorProfessorRelator(Professor professor) {
        return processoRepository.findByRelator(professor);
    }

    public List<Processo> listarProcessosDoColegiado(ProcessoDTOFiltro filtro, Long colegiadoId) {
        return processoRepository.filtrarPorColegiado(filtro, colegiadoId);
    }

    public long contarProcessosPorStatus(Long colegiadoId, StatusProcessoEnum status) {
        ProcessoDTOFiltro filtro = new ProcessoDTOFiltro();
        filtro.setStatus(status.name());
        
        return processoRepository.filtrarPorColegiado(filtro, colegiadoId).size();
    }

    public Processo distribuirProcesso(Long processoId, Long professorId) {
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado"));
        
        if (processo.getStatus() != StatusProcessoEnum.CRIADO) {
            throw new RuntimeException("Processo não pode ser distribuído. Status atual: " + processo.getStatus());
        }
        
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
        
        processo.setRelator(professor);
        processo.setStatus(StatusProcessoEnum.DISTRIBUIDO);
        processo.setDataDistribuicao(LocalDate.now());
        
        return processoRepository.save(processo);
    }
}
