package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.model.Aluno;
import br.edu.ifpb.pweb2.veredictum.model.Professor;
import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import br.edu.ifpb.pweb2.veredictum.repository.AlunoRepository;
import br.edu.ifpb.pweb2.veredictum.repository.ProfessorRepository;
import br.edu.ifpb.pweb2.veredictum.repository.UsuarioRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProfessorRepository professorRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Validator validator;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Aluno aluno;
    private Professor professor;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@test.com");
        usuario.setSenha("senha123");
        usuario.setTelefone("83999999999");

        aluno = new Aluno();
        aluno.setId(2L);
        aluno.setNome("Maria Aluna");
        aluno.setEmail("maria@aluno.ifpb.edu.br");
        aluno.setSenha("senha123");
        aluno.setRole(RoleEnum.ALUNO);
        aluno.setMatricula("20231234567");

        professor = new Professor();
        professor.setId(3L);
        professor.setNome("Carlos Professor");
        professor.setEmail("carlos@ifpb.edu.br");
        professor.setSenha("senha123");
        professor.setRole(RoleEnum.PROFESSOR);
        professor.setMatricula("20201001234");
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarTodosUsuarios() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuario, aluno, professor);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        List<Usuario> resultado = usuarioService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void deveBuscarUsuarioPorId() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.buscarPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("João Silva", resultado.get().getNome());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve buscar usuário por email")
    void deveBuscarUsuarioPorEmail() {
        // Arrange
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.buscarPorEmail("joao@test.com");

        // Assert
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        verify(usuarioRepository, times(1)).findByEmail("joao@test.com");
    }

    @Test
    @DisplayName("Deve contar usuários")
    void deveContarUsuarios() {
        // Arrange
        when(usuarioRepository.count()).thenReturn(5L);

        // Act
        long resultado = usuarioService.contarUsuarios();

        // Assert
        assertEquals(5L, resultado);
        verify(usuarioRepository, times(1)).count();
    }

    @Test
    @DisplayName("Deve salvar aluno com sucesso")
    void deveSalvarAlunoComSucesso() {
        // Arrange
        Usuario novoAluno = new Usuario();
        novoAluno.setNome("Pedro Aluno");
        novoAluno.setEmail("pedro@aluno.ifpb.edu.br");
        novoAluno.setSenha("senha123");
        novoAluno.setRole(RoleEnum.ALUNO);
        
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(validator.validate(any(Aluno.class))).thenReturn(new HashSet<>());
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Usuario resultado = usuarioService.salvar(novoAluno, "20231234567");

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado instanceof Aluno);
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(alunoRepository, times(1)).save(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve salvar professor com sucesso")
    void deveSalvarProfessorComSucesso() {
        // Arrange
        Usuario novoProfessor = new Usuario();
        novoProfessor.setNome("Ana Professor");
        novoProfessor.setEmail("ana@ifpb.edu.br");
        novoProfessor.setSenha("senha123");
        novoProfessor.setRole(RoleEnum.PROFESSOR);
        
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(validator.validate(any(Professor.class))).thenReturn(new HashSet<>());
        when(professorRepository.save(any(Professor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Usuario resultado = usuarioService.salvar(novoProfessor, "20201001234");

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado instanceof Professor);
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(professorRepository, times(1)).save(any(Professor.class));
    }

    @Test
    @DisplayName("Deve salvar coordenador com flag ehCoordenador true")
    void deveSalvarCoordenadorComFlagTrue() {
        // Arrange
        Usuario novoCoordenador = new Usuario();
        novoCoordenador.setNome("Coordenador");
        novoCoordenador.setEmail("coord@ifpb.edu.br");
        novoCoordenador.setSenha("senha123");
        novoCoordenador.setRole(RoleEnum.COORDENADOR);
        
        when(passwordEncoder.encode("senha123")).thenReturn("senhaEncriptada");
        when(validator.validate(any(Professor.class))).thenReturn(new HashSet<>());
        when(professorRepository.save(any(Professor.class))).thenAnswer(invocation -> {
            Professor p = invocation.getArgument(0);
            assertTrue(p.isEhCoordenador());
            return p;
        });

        // Act
        Usuario resultado = usuarioService.salvar(novoCoordenador, "20201001234");

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado instanceof Professor);
        assertTrue(((Professor) resultado).isEhCoordenador());
        verify(professorRepository, times(1)).save(any(Professor.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar aluno com validação inválida")
    void deveLancarExcecaoAoSalvarAlunoInvalido() {
        // Arrange
        Usuario novoAluno = new Usuario();
        novoAluno.setRole(RoleEnum.ALUNO);
        novoAluno.setSenha("senha123");
        
        when(passwordEncoder.encode(any())).thenReturn("senhaEncriptada");
        
        Set<ConstraintViolation<Aluno>> violations = new HashSet<>();
        ConstraintViolation<Aluno> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Matrícula inválida");
        violations.add(violation);
        
        when(validator.validate(any(Aluno.class))).thenReturn(violations);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> usuarioService.salvar(novoAluno, "invalida"));
        
        assertEquals("Matrícula inválida", exception.getMessage());
        verify(alunoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar aluno")
    void deveAtualizarAluno() {
        // Arrange
        aluno.setMatricula("20231234567");
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(aluno));
        when(validator.validate(any(Aluno.class))).thenReturn(new HashSet<>());
        when(usuarioRepository.save(any(Aluno.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario alunoAtualizado = new Usuario();
        alunoAtualizado.setNome("Maria Silva Atualizada");
        alunoAtualizado.setEmail("maria.nova@aluno.ifpb.edu.br");
        alunoAtualizado.setRole(RoleEnum.ALUNO);

        // Act
        Usuario resultado = usuarioService.atualizar(2L, alunoAtualizado, "20239999999");

        // Assert
        assertNotNull(resultado);
        assertEquals("Maria Silva Atualizada", resultado.getNome());
        verify(usuarioRepository, times(1)).findById(2L);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve excluir usuário")
    void deveExcluirUsuario() {
        // Arrange
        doNothing().when(usuarioRepository).deleteById(1L);

        // Act
        usuarioService.excluir(1L);

        // Assert
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve verificar se email existe")
    void deveVerificarSeEmailExiste() {
        // Arrange
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(usuario);
        when(usuarioRepository.findByEmail("naoexiste@test.com")).thenReturn(null);

        // Act & Assert
        assertTrue(usuarioService.emailExiste("joao@test.com"));
        assertFalse(usuarioService.emailExiste("naoexiste@test.com"));
        verify(usuarioRepository, times(1)).findByEmail("joao@test.com");
        verify(usuarioRepository, times(1)).findByEmail("naoexiste@test.com");
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário inexistente")
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setNome("Teste");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> usuarioService.atualizar(999L, usuarioAtualizado, "12345"));
        
        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
        verify(usuarioRepository, times(1)).findById(999L);
        verify(usuarioRepository, never()).save(any());
    }
}
