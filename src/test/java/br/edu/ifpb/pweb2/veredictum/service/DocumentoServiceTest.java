package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.model.Documento;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import br.edu.ifpb.pweb2.veredictum.repository.DocumentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do DocumentoService")
class DocumentoServiceTest {

    @Mock
    private DocumentoRepository documentoRepository;

    @InjectMocks
    private DocumentoService documentoService;

    private Processo processo;
    private Usuario usuario;
    private MultipartFile arquivo;

    @BeforeEach
    void setUp() {
        processo = new Processo();
        processo.setId(1L);
        processo.setDocumentos(new ArrayList<>());

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");

        arquivo = new MockMultipartFile(
            "arquivo",
            "documento.pdf",
            "application/pdf",
            "Conteúdo do arquivo".getBytes()
        );
    }

    @Test
    @DisplayName("Deve anexar documento com sucesso")
    void deveAnexarDocumentoComSucesso() throws IOException {
        // Arrange
        when(documentoRepository.save(any(Documento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        documentoService.anexarDocumento(processo, arquivo, usuario);

        // Assert
        ArgumentCaptor<Documento> captor = ArgumentCaptor.forClass(Documento.class);
        verify(documentoRepository, times(1)).save(captor.capture());
        
        Documento documentoSalvo = captor.getValue();
        assertEquals("documento.pdf", documentoSalvo.getNomeArquivo());
        assertEquals("application/pdf", documentoSalvo.getTipoArquivo());
        assertEquals(arquivo.getSize(), documentoSalvo.getTamanho());
        assertNotNull(documentoSalvo.getDataUpload());
        assertEquals(processo, documentoSalvo.getProcesso());
        assertEquals(usuario, documentoSalvo.getUploadPor());
        assertEquals(1, processo.getDocumentos().size());
    }

    @Test
    @DisplayName("Deve lançar exceção quando limite de documentos é atingido")
    void deveLancarExcecaoQuandoLimiteAtingido() throws IOException {
        // Arrange
        for (int i = 0; i < 3; i++) {
            Documento doc = new Documento();
            doc.setId((long) i);
            processo.getDocumentos().add(doc);
        }

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> documentoService.anexarDocumento(processo, arquivo, usuario));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Limite máximo de 3 documentos atingido"));
        verify(documentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve definir corretamente os dados do arquivo")
    void deveDefinirCorretamenteDadosDoArquivo() throws IOException {
        // Arrange
        when(documentoRepository.save(any(Documento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        documentoService.anexarDocumento(processo, arquivo, usuario);

        // Assert
        ArgumentCaptor<Documento> captor = ArgumentCaptor.forClass(Documento.class);
        verify(documentoRepository).save(captor.capture());
        
        Documento documentoSalvo = captor.getValue();
        assertArrayEquals(arquivo.getBytes(), documentoSalvo.getConteudo());
        assertEquals(arquivo.getOriginalFilename(), documentoSalvo.getNomeArquivo());
        assertEquals(arquivo.getContentType(), documentoSalvo.getTipoArquivo());
        assertEquals(arquivo.getSize(), documentoSalvo.getTamanho());
    }

    @Test
    @DisplayName("Deve adicionar documento à lista de documentos do processo")
    void deveAdicionarDocumentoAoProcesso() throws IOException {
        // Arrange
        when(documentoRepository.save(any(Documento.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        assertEquals(0, processo.getDocumentos().size());

        // Act
        documentoService.anexarDocumento(processo, arquivo, usuario);

        // Assert
        assertEquals(1, processo.getDocumentos().size());
        assertTrue(processo.getDocumentos().stream()
            .anyMatch(doc -> doc.getNomeArquivo().equals("documento.pdf")));
    }

    @Test
    @DisplayName("Deve permitir anexar até 3 documentos")
    void devePermitirAnexarAte3Documentos() throws IOException {
        // Arrange
        when(documentoRepository.save(any(Documento.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        MultipartFile arquivo1 = new MockMultipartFile("arquivo1", "doc1.pdf", "application/pdf", "content1".getBytes());
        MultipartFile arquivo2 = new MockMultipartFile("arquivo2", "doc2.pdf", "application/pdf", "content2".getBytes());

        // Act
        documentoService.anexarDocumento(processo, arquivo1, usuario);
        documentoService.anexarDocumento(processo, arquivo2, usuario);
        documentoService.anexarDocumento(processo, arquivo, usuario);

        // Assert
        assertEquals(3, processo.getDocumentos().size());
        verify(documentoRepository, times(3)).save(any(Documento.class));
    }
}
