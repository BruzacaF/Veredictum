package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.model.Assunto;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.repository.AssuntoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AssuntoService")
class AssuntoServiceTest {

    @Mock
    private AssuntoRepository assuntoRepository;

    @InjectMocks
    private AssuntoService assuntoService;

    private Assunto assunto;

    @BeforeEach
    void setUp() {
        assunto = new Assunto();
        assunto.setId(1L);
        assunto.setNome("Trancamento de Disciplina");
        assunto.setProcessos(new ArrayList<>());
    }

    @Test
    @DisplayName("Deve listar todos os assuntos")
    void deveListarTodosAssuntos() {
        // Arrange
        Assunto assunto2 = new Assunto();
        assunto2.setId(2L);
        assunto2.setNome("Matrícula Extraordinária");
        List<Assunto> assuntos = Arrays.asList(assunto, assunto2);
        when(assuntoRepository.findAll()).thenReturn(assuntos);

        // Act
        List<Assunto> resultado = assuntoService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(assuntoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar assunto por ID")
    void deveBuscarAssuntoPorId() {
        // Arrange
        when(assuntoRepository.findById(1L)).thenReturn(Optional.of(assunto));

        // Act
        Assunto resultado = assuntoService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals("Trancamento de Disciplina", resultado.getNome());
        verify(assuntoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando assunto não é encontrado")
    void deveLancarExcecaoQuandoAssuntoNaoEncontrado() {
        // Arrange
        when(assuntoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> assuntoService.buscarPorId(999L));
        
        assertEquals("Assunto não encontrado", exception.getMessage());
        verify(assuntoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve salvar assunto")
    void deveSalvarAssunto() {
        // Arrange
        when(assuntoRepository.save(assunto)).thenReturn(assunto);

        // Act
        Assunto resultado = assuntoService.salvar(assunto);

        // Assert
        assertNotNull(resultado);
        assertEquals("Trancamento de Disciplina", resultado.getNome());
        verify(assuntoRepository, times(1)).save(assunto);
    }

    @Test
    @DisplayName("Deve atualizar assunto")
    void deveAtualizarAssunto() {
        // Arrange
        assunto.setNome("Nome atualizado");
        when(assuntoRepository.findById(1L)).thenReturn(Optional.of(assunto));
        when(assuntoRepository.save(assunto)).thenReturn(assunto);

        // Act
        Assunto resultado = assuntoService.atualizar(assunto);

        // Assert
        assertNotNull(resultado);
        assertEquals("Nome atualizado", resultado.getNome());
        verify(assuntoRepository, times(1)).findById(1L);
        verify(assuntoRepository, times(1)).save(assunto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar assunto sem ID")
    void deveLancarExcecaoAoAtualizarAssuntoSemId() {
        // Arrange
        assunto.setId(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> assuntoService.atualizar(assunto));
        
        assertEquals("ID do assunto não pode ser nulo para atualização", exception.getMessage());
        verify(assuntoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve excluir assunto sem processos vinculados")
    void deveExcluirAssuntoSemProcessos() {
        // Arrange
        when(assuntoRepository.findById(1L)).thenReturn(Optional.of(assunto));
        doNothing().when(assuntoRepository).deleteById(1L);

        // Act
        assuntoService.excluir(1L);

        // Assert
        verify(assuntoRepository, times(1)).findById(1L);
        verify(assuntoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir assunto com processos vinculados")
    void deveLancarExcecaoAoExcluirAssuntoComProcessos() {
        // Arrange
        Processo processo = new Processo();
        assunto.getProcessos().add(processo);
        when(assuntoRepository.findById(1L)).thenReturn(Optional.of(assunto));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> assuntoService.excluir(1L));
        
        assertEquals("Não é possível excluir assunto com processos vinculados", exception.getMessage());
        verify(assuntoRepository, times(1)).findById(1L);
        verify(assuntoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve contar assuntos")
    void deveContarAssuntos() {
        // Arrange
        when(assuntoRepository.count()).thenReturn(5L);

        // Act
        long resultado = assuntoService.contarAssuntos();

        // Assert
        assertEquals(5L, resultado);
        verify(assuntoRepository, times(1)).count();
    }
}
