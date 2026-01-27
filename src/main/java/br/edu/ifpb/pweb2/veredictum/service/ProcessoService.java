package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTO;
import br.edu.ifpb.pweb2.veredictum.dto.ProcessoDTOFiltro;
import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.model.*;
import br.edu.ifpb.pweb2.veredictum.repository.DocumentoRepository;
import br.edu.ifpb.pweb2.veredictum.repository.ProcessoRepository;
import br.edu.ifpb.pweb2.veredictum.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class ProcessoService {
    @Autowired
    ProcessoRepository processoRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private DocumentoService documentoService;
    @Autowired
    private DocumentoRepository documentoRepository;
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

            String ano = String.valueOf(LocalDate.now().getYear());
            Long sequencial = processoRepository.count() + 1;
            processo.setNumero(ano + "-" + String.format("%03d", sequencial));

            processo = processoRepository.save(processo);

            MultipartFile arquivo = processoDTO.getArquivo();

            if (arquivo != null && !arquivo.isEmpty()) {
                salvarDocumento(arquivo, processo, aluno);
            }

            return processo;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar processo" + e.getMessage(), e);
        }
    }

    private void salvarDocumento(MultipartFile file, Processo processo, Aluno aluno) throws Exception {
        Documento documento = new Documento();
        documento.setNomeArquivo(file.getOriginalFilename());
        documento.setTipoArquivo(file.getContentType());
        documento.setTamanho(file.getSize());
        documento.setDataUpload(LocalDateTime.now());
        documento.setProcesso(processo);
        documento.setUploadPor(aluno);

        try {
            documento.setConteudo(file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar processo" + e.getMessage(), e);
        }

        documentoRepository.save(documento);
    }

    public Processo buscarPorId(long id) {
        return  processoRepository.findById(id).orElse(null);
    }

    public void anexarArquivo(Long processoId,
                              MultipartFile arquivo,
                              Usuario usuario) throws IOException {

        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (processo.getStatus() != StatusProcessoEnum.CRIADO) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é possível adicionar documentos após a distribuição"
            );
        }

        documentoService.anexarDocumento(processo, arquivo, usuario);
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

    @Transactional(readOnly = true)
    public Processo buscarPorIdReadOnly(Long id) {
        return processoRepository.findById(id).orElseThrow(() -> new RuntimeException("Processo Não encontrado"));

    }

    public boolean processoTemDocumento(Long processoId){
        return documentoRepository.existsById(processoId);
    }

    public Page<Processo> buscarPorAlunoPaginado(Aluno aluno, Pageable pageable) {
        ProcessoDTOFiltro filtro = new ProcessoDTOFiltro();
        return processoRepository.filtrar(filtro, aluno.getId(), RoleEnum.ALUNO, pageable);
    }

}
