package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.model.Colegiado;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.repository.ColegiadoRepository;
import br.edu.ifpb.pweb2.veredictum.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ColegiadoService")
class ColegiadoServiceTest {

    @Mock
    private ColegiadoRepository colegiadoRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @InjectMocks
    private ColegiadoService colegiadoService;

    private Colegiado colegiado;
    private Professor professor1;
    private Professor professor2;

    @BeforeEach
    void setUp() {
        professor1 = new Professor();
        professor1.setId(1L);
        professor1.setNome("Prof. João");
        professor1.setColegiados(new HashSet<>());

        professor2 = new Professor();
        professor2.setId(2L);
        professor2.setNome("Prof. Maria");
        professor2.setColegiados(new HashSet<>());

        colegiado = new Colegiado();
        colegiado.setId(1L);
        colegiado.setDescricao("Colegiado de Informática");
        colegiado.setDataInicio(LocalDate.of(2024, 1, 1));
        colegiado.setMembros(new HashSet<>());
    }

    @Test
    @DisplayName("Deve listar todos os colegiados")
    void deveListarTodosColegiados() {
        // Arrange
        List<Colegiado> colegiados = Arrays.asList(colegiado);
        when(colegiadoRepository.findAll()).thenReturn(colegiados);

        // Act
        List<Colegiado> resultado = colegiadoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(colegiadoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar colegiado por ID")
    void deveBuscarColegiadoPorId() {
        // Arrange
        when(colegiadoRepository.findById(1L)).thenReturn(Optional.of(colegiado));

        // Act
        Colegiado resultado = colegiadoService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("Colegiado de Informática", resultado.getDescricao());
        verify(colegiadoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando colegiado não é encontrado")
    void deveLancarExcecaoQuandoColegiadoNaoEncontrado() {
        // Arrange
        when(colegiadoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> colegiadoService.buscarPorId(999L));

        assertEquals("Colegiado não encontrado", exception.getMessage());
        verify(colegiadoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve salvar colegiado com membros")
    void deveSalvarColegiadoComMembros() {
        // Arrange
        List<Long> membrosIds = Arrays.asList(1L, 2L);
        List<Professor> professores = Arrays.asList(professor1, professor2);
        
        when(professorRepository.findAllById(membrosIds)).thenReturn(professores);
        when(colegiadoRepository.save(any(Colegiado.class))).thenReturn(colegiado);

        // Act
        Colegiado resultado = colegiadoService.salvar(colegiado, membrosIds);

        // Assert
        assertNotNull(resultado);
        verify(professorRepository, times(1)).findAllById(membrosIds);
        verify(colegiadoRepository, times(1)).save(any(Colegiado.class));
    }

    @Test
    @DisplayName("Deve salvar colegiado sem membros")
    void deveSalvarColegiadoSemMembros() {
        // Arrange
        when(colegiadoRepository.save(any(Colegiado.class))).thenReturn(colegiado);

        // Act
        Colegiado resultado = colegiadoService.salvar(colegiado, null);

        // Assert
        assertNotNull(resultado);
        verify(professorRepository, never()).findAllById(any());
        verify(colegiadoRepository, times(1)).save(any(Colegiado.class));
    }

    @Test
    @DisplayName("Deve atualizar colegiado")
    void deveAtualizarColegiado() {
        // Arrange
        colegiado.getMembros().add(professor1);
        professor1.getColegiados().add(colegiado);
        
        List<Long> novosMembrosIds = Arrays.asList(2L);
        List<Professor> novosMembros = Arrays.asList(professor2);
        
        when(colegiadoRepository.findById(1L)).thenReturn(Optional.of(colegiado));
        when(professorRepository.findAllById(novosMembrosIds)).thenReturn(novosMembros);
        when(colegiadoRepository.save(any(Colegiado.class))).thenReturn(colegiado);

        // Act
        Colegiado atualizado = new Colegiado();
        atualizado.setId(1L);
        atualizado.setDescricao("Descrição atualizada");
        atualizado.setDataInicio(LocalDate.of(2024, 6, 1));
        
        Colegiado resultado = colegiadoService.atualizar(atualizado, novosMembrosIds);

        // Assert
        assertNotNull(resultado);
        verify(colegiadoRepository, times(1)).findById(1L);
        verify(professorRepository, times(1)).findAllById(novosMembrosIds);
        verify(colegiadoRepository, times(1)).save(any(Colegiado.class));
    }

    @Test
    @DisplayName("Deve excluir colegiado")
    void deveExcluirColegiado() {
        // Arrange
        colegiado.getMembros().add(professor1);
        professor1.getColegiados().add(colegiado);
        
        when(colegiadoRepository.findById(1L)).thenReturn(Optional.of(colegiado));
        doNothing().when(colegiadoRepository).delete(colegiado);

        // Act
        colegiadoService.excluir(1L);

        // Assert
        verify(colegiadoRepository, times(1)).findById(1L);
        verify(colegiadoRepository, times(1)).delete(colegiado);
    }

    @Test
    @DisplayName("Deve contar colegiados")
    void deveContarColegiados() {
        // Arrange
        when(colegiadoRepository.count()).thenReturn(3L);

        // Act
        long resultado = colegiadoService.contarColegiados();

        // Assert
        assertEquals(3L, resultado);
        verify(colegiadoRepository, times(1)).count();
    }

    @Test
    @DisplayName("Deve listar professores disponíveis")
    void deveListarProfessoresDisponiveis() {
        // Arrange
        List<Professor> professores = Arrays.asList(professor1, professor2);
        when(professorRepository.findAllByOrderByNomeAsc()).thenReturn(professores);

        // Act
        List<Professor> resultado = colegiadoService.listarProfessoresDisponiveis();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(professorRepository, times(1)).findAllByOrderByNomeAsc();
    }
}
