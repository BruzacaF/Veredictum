package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ProfessorService")
class ProfessorServiceTest {

    @Mock
    private ProfessorRepository professorRepository;

    @InjectMocks
    private ProfessorService professorService;

    private Professor professor1;
    private Professor professor2;

    @BeforeEach
    void setUp() {
        professor1 = new Professor();
        professor1.setId(1L);
        professor1.setNome("Prof. João Silva");
        professor1.setMatricula("123456");
        professor1.setEmail("joao@ifpb.edu.br");

        professor2 = new Professor();
        professor2.setId(2L);
        professor2.setNome("Prof. Maria Santos");
        professor2.setMatricula("789012");
        professor2.setEmail("maria@ifpb.edu.br");
    }

    @Test
    @DisplayName("Deve buscar professores por ID do colegiado")
    void deveBuscarProfessoresPorColegiadoId() {
        // Arrange
        Long colegiadoId = 1L;
        List<Professor> professores = Arrays.asList(professor1, professor2);
        when(professorRepository.findByColegiadoId(colegiadoId)).thenReturn(professores);

        // Act
        List<Professor> resultado = professorService.buscarPorColegiadoId(colegiadoId);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Prof. João Silva", resultado.get(0).getNome());
        assertEquals("Prof. Maria Santos", resultado.get(1).getNome());
        verify(professorRepository, times(1)).findByColegiadoId(colegiadoId);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há professores no colegiado")
    void deveRetornarListaVaziaQuandoNaoHaProfessores() {
        // Arrange
        Long colegiadoId = 999L;
        when(professorRepository.findByColegiadoId(colegiadoId)).thenReturn(Arrays.asList());

        // Act
        List<Professor> resultado = professorService.buscarPorColegiadoId(colegiadoId);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(professorRepository, times(1)).findByColegiadoId(colegiadoId);
    }

    @Test
    @DisplayName("Deve buscar apenas um professor quando colegiado tem um membro")
    void deveBuscarApenaUmProfessor() {
        // Arrange
        Long colegiadoId = 2L;
        List<Professor> professores = Arrays.asList(professor1);
        when(professorRepository.findByColegiadoId(colegiadoId)).thenReturn(professores);

        // Act
        List<Professor> resultado = professorService.buscarPorColegiadoId(colegiadoId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Prof. João Silva", resultado.get(0).getNome());
        verify(professorRepository, times(1)).findByColegiadoId(colegiadoId);
    }
}
