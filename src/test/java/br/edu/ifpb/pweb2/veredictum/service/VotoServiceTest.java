package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.enums.TipoDecisao;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Voto;
import br.edu.ifpb.pweb2.veredictum.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do VotoService")
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @InjectMocks
    private VotoService votoService;

    private Processo processo;
    private Professor professor;
    private Voto voto;

    @BeforeEach
    void setUp() {
        processo = new Processo();
        processo.setId(1L);
        processo.setNumero("2024-001");

        professor = new Professor();
        professor.setId(1L);
        professor.setNome("Prof. João");

        voto = new Voto();
        voto.setId(1L);
        voto.setProcesso(processo);
        voto.setProfessor(professor);
        voto.setVoto(TipoDecisao.DEFERIMENTO);
        voto.setJustificativa("Justificativa do voto");
    }

    @Test
    @DisplayName("Deve registrar novo voto")
    void deveRegistrarNovoVoto() {
        // Arrange
        when(votoRepository.findByProfessorAndProcesso(professor, processo)).thenReturn(Optional.empty());
        when(votoRepository.save(any(Voto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Voto resultado = votoService.registrarVoto(processo, professor, TipoDecisao.DEFERIMENTO, "Justificativa");

        // Assert
        assertNotNull(resultado);
        assertEquals(TipoDecisao.DEFERIMENTO, resultado.getVoto());
        assertEquals("Justificativa", resultado.getJustificativa());
        assertEquals(processo, resultado.getProcesso());
        assertEquals(professor, resultado.getProfessor());
        verify(votoRepository, times(1)).findByProfessorAndProcesso(professor, processo);
        verify(votoRepository, times(1)).save(any(Voto.class));
    }

    @Test
    @DisplayName("Deve atualizar voto existente")
    void deveAtualizarVotoExistente() {
        // Arrange
        when(votoRepository.findByProfessorAndProcesso(professor, processo)).thenReturn(Optional.of(voto));
        when(votoRepository.save(any(Voto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Voto resultado = votoService.registrarVoto(processo, professor, TipoDecisao.INDEFERIMENTO, "Nova justificativa");

        // Assert
        assertNotNull(resultado);
        assertEquals(TipoDecisao.INDEFERIMENTO, resultado.getVoto());
        assertEquals("Nova justificativa", resultado.getJustificativa());
        verify(votoRepository, times(1)).findByProfessorAndProcesso(professor, processo);
        verify(votoRepository, times(1)).save(voto);
    }

    @Test
    @DisplayName("Deve verificar se professor já votou")
    void deveVerificarSeProfessorJaVotou() {
        // Arrange
        when(votoRepository.existsByProcessoIdAndProfessorId(1L, 1L)).thenReturn(true);
        when(votoRepository.existsByProcessoIdAndProfessorId(1L, 2L)).thenReturn(false);

        // Act & Assert
        assertTrue(votoService.professorJaVotou(1L, 1L));
        assertFalse(votoService.professorJaVotou(1L, 2L));
        verify(votoRepository, times(1)).existsByProcessoIdAndProfessorId(1L, 1L);
        verify(votoRepository, times(1)).existsByProcessoIdAndProfessorId(1L, 2L);
    }

    @Test
    @DisplayName("Deve buscar voto do professor")
    void deveBuscarVotoDoProfessor() {
        // Arrange
        when(votoRepository.findByProfessorAndProcesso(professor, processo)).thenReturn(Optional.of(voto));

        // Act
        Optional<Voto> resultado = votoService.buscarVotoDoProfessor(professor, processo);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(voto, resultado.get());
        assertEquals(TipoDecisao.DEFERIMENTO, resultado.get().getVoto());
        verify(votoRepository, times(1)).findByProfessorAndProcesso(professor, processo);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando não há voto")
    void deveRetornarOptionalVazioQuandoNaoHaVoto() {
        // Arrange
        when(votoRepository.findByProfessorAndProcesso(professor, processo)).thenReturn(Optional.empty());

        // Act
        Optional<Voto> resultado = votoService.buscarVotoDoProfessor(professor, processo);

        // Assert
        assertFalse(resultado.isPresent());
        verify(votoRepository, times(1)).findByProfessorAndProcesso(professor, processo);
    }

    @Test
    @DisplayName("Deve registrar voto deferido")
    void deveRegistrarVotoDeferido() {
        // Arrange
        when(votoRepository.findByProfessorAndProcesso(professor, processo)).thenReturn(Optional.empty());
        when(votoRepository.save(any(Voto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Voto resultado = votoService.registrarVoto(processo, professor, TipoDecisao.DEFERIMENTO, "Procedente");

        // Assert
        assertNotNull(resultado);
        assertEquals(TipoDecisao.DEFERIMENTO, resultado.getVoto());
        verify(votoRepository, times(1)).save(any(Voto.class));
    }

    @Test
    @DisplayName("Deve registrar voto indeferido")
    void deveRegistrarVotoIndeferido() {
        // Arrange
        when(votoRepository.findByProfessorAndProcesso(professor, processo)).thenReturn(Optional.empty());
        when(votoRepository.save(any(Voto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Voto resultado = votoService.registrarVoto(processo, professor, TipoDecisao.INDEFERIMENTO, "Improcedente");

        // Assert
        assertNotNull(resultado);
        assertEquals(TipoDecisao.INDEFERIMENTO, resultado.getVoto());
        verify(votoRepository, times(1)).save(any(Voto.class));
    }

    @Test
    @DisplayName("Deve registrar voto com justificativa nula")
    void deveRegistrarVotoComJustificativaNula() {
        // Arrange
        when(votoRepository.findByProfessorAndProcesso(professor, processo)).thenReturn(Optional.empty());
        when(votoRepository.save(any(Voto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Voto resultado = votoService.registrarVoto(processo, professor, TipoDecisao.DEFERIMENTO, null);

        // Assert
        assertNotNull(resultado);
        assertNull(resultado.getJustificativa());
        verify(votoRepository, times(1)).save(any(Voto.class));
    }
}
