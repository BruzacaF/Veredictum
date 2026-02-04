package br.edu.ifpb.pweb2.veredictum.service;

import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import br.edu.ifpb.pweb2.veredictum.repository.UsuarioRepository;
import br.edu.ifpb.pweb2.veredictum.security.UsuarioDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioDetailsService")
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@test.com");
        usuario.setSenha("senhaEncriptada");
        usuario.setRole(RoleEnum.ALUNO);
    }

    @Test
    @DisplayName("Deve carregar usuário por username (email)")
    void deveCarregarUsuarioPorEmail() {
        // Arrange
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(usuario);

        // Act
        UserDetails resultado = usuarioDetailsService.loadUserByUsername("joao@test.com");

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado instanceof UsuarioDetails);
        assertEquals("joao@test.com", resultado.getUsername());
        assertEquals("senhaEncriptada", resultado.getPassword());
        verify(usuarioRepository, times(1)).findByEmail("joao@test.com");
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando usuário não é encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Arrange
        when(usuarioRepository.findByEmail("naoexiste@test.com")).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
            () -> usuarioDetailsService.loadUserByUsername("naoexiste@test.com"));

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
        assertTrue(exception.getMessage().contains("naoexiste@test.com"));
        verify(usuarioRepository, times(1)).findByEmail("naoexiste@test.com");
    }

    @Test
    @DisplayName("Deve retornar UserDetails com as authorities corretas")
    void deveRetornarUserDetailsComAuthoritiesCorretas() {
        // Arrange
        usuario.setRole(RoleEnum.PROFESSOR);
        when(usuarioRepository.findByEmail("joao@test.com")).thenReturn(usuario);

        // Act
        UserDetails resultado = usuarioDetailsService.loadUserByUsername("joao@test.com");

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getAuthorities());
        assertFalse(resultado.getAuthorities().isEmpty());
        verify(usuarioRepository, times(1)).findByEmail("joao@test.com");
    }

    @Test
    @DisplayName("Deve lidar com email em branco")
    void deveLidarComEmailEmBranco() {
        // Arrange
        when(usuarioRepository.findByEmail("")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
            () -> usuarioDetailsService.loadUserByUsername(""));
        
        verify(usuarioRepository, times(1)).findByEmail("");
    }

    @Test
    @DisplayName("Deve carregar diferentes tipos de usuários")
    void deveCarregarDiferentesTiposDeUsuarios() {
        // Test ALUNO
        usuario.setRole(RoleEnum.ALUNO);
        when(usuarioRepository.findByEmail("aluno@test.com")).thenReturn(usuario);
        UserDetails alunoDetails = usuarioDetailsService.loadUserByUsername("aluno@test.com");
        assertNotNull(alunoDetails);

        // Test PROFESSOR
        usuario.setRole(RoleEnum.PROFESSOR);
        when(usuarioRepository.findByEmail("prof@test.com")).thenReturn(usuario);
        UserDetails profDetails = usuarioDetailsService.loadUserByUsername("prof@test.com");
        assertNotNull(profDetails);

        // Test COORDENADOR
        usuario.setRole(RoleEnum.COORDENADOR);
        when(usuarioRepository.findByEmail("coord@test.com")).thenReturn(usuario);
        UserDetails coordDetails = usuarioDetailsService.loadUserByUsername("coord@test.com");
        assertNotNull(coordDetails);

        verify(usuarioRepository, times(3)).findByEmail(anyString());
    }
}
