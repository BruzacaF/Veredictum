package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.model.Aluno;
import br.edu.ifpb.pweb2.veredictum.repository.AlunoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AlunoService")
class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private AlunoService alunoService;

    private Aluno aluno;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João Silva");
        aluno.setMatricula("20231234567");
        aluno.setEmail("joao@aluno.ifpb.edu.br");
    }

    @Test
    @DisplayName("Deve listar todos os alunos")
    void deveListarTodosAlunos() {
        // Arrange
        Aluno aluno2 = new Aluno();
        aluno2.setId(2L);
        aluno2.setNome("Maria Santos");
        List<Aluno> alunos = Arrays.asList(aluno, aluno2);
        when(alunoRepository.findAll()).thenReturn(alunos);

        // Act
        List<Aluno> resultado = alunoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(alunoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar aluno por ID")
    void deveBuscarAlunoPorId() {
        // Arrange
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

        // Act
        Optional<Aluno> resultado = alunoService.buscarPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("João Silva", resultado.get().getNome());
        assertEquals("20231234567", resultado.get().getMatricula());
        verify(alunoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando aluno não existe")
    void deveRetornarOptionalVazioQuandoAlunoNaoExiste() {
        // Arrange
        when(alunoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Aluno> resultado = alunoService.buscarPorId(999L);

        // Assert
        assertFalse(resultado.isPresent());
        verify(alunoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve buscar aluno por matrícula")
    void deveBuscarAlunoPorMatricula() {
        // Arrange
        when(alunoRepository.findByMatricula("20231234567")).thenReturn(aluno);

        // Act
        Aluno resultado = alunoService.buscarPorMatricula("20231234567");

        // Assert
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals("20231234567", resultado.getMatricula());
        verify(alunoRepository, times(1)).findByMatricula("20231234567");
    }

    @Test
    @DisplayName("Deve retornar null quando matrícula não existe")
    void deveRetornarNullQuandoMatriculaNaoExiste() {
        // Arrange
        when(alunoRepository.findByMatricula("99999999999")).thenReturn(null);

        // Act
        Aluno resultado = alunoService.buscarPorMatricula("99999999999");

        // Assert
        assertNull(resultado);
        verify(alunoRepository, times(1)).findByMatricula("99999999999");
    }

    @Test
    @DisplayName("Deve contar alunos")
    void deveContarAlunos() {
        // Arrange
        when(alunoRepository.count()).thenReturn(10L);

        // Act
        long resultado = alunoService.contarAlunos();

        // Assert
        assertEquals(10L, resultado);
        verify(alunoRepository, times(1)).count();
    }
}
