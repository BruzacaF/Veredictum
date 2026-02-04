package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.model.Documento;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import br.edu.ifpb.pweb2.veredictum.repository.DocumentoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Transactional
public class DocumentoService {

    private static final int LIMITE_DOCUMENTOS = 3;

    private final DocumentoRepository documentoRepository;

    public DocumentoService(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    public void anexarDocumento(Processo processo,
                                MultipartFile file,
                                Usuario usuario) throws IOException {

        if (processo.getDocumentos().size() >= LIMITE_DOCUMENTOS) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Limite máximo de 3 documentos atingido"
            );
        }

        Documento doc = new Documento();
        doc.setNomeArquivo(file.getOriginalFilename());
        doc.setTipoArquivo(file.getContentType());
        doc.setTamanho(file.getSize());
        doc.setConteudo(file.getBytes());
        doc.setDataUpload(LocalDateTime.now());
        doc.setProcesso(processo);
        doc.setUploadPor(usuario);

        processo.getDocumentos().add(doc);
        documentoRepository.save(doc);
    }
}
