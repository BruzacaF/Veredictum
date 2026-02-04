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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private Validator validator;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public long contarUsuarios() {
        return usuarioRepository.count();
    }

    @Transactional
    public Usuario salvar(Usuario usuario, String matricula) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        
        RoleEnum role = usuario.getRole();
        
        if (role == RoleEnum.ALUNO) {
            Aluno aluno = new Aluno();
            aluno.setNome(usuario.getNome());
            aluno.setEmail(usuario.getEmail());
            aluno.setTelefone(usuario.getTelefone());
            aluno.setSenha(usuario.getSenha());
            aluno.setRole(RoleEnum.ALUNO);
            aluno.setMatricula(matricula);
            
            // Valida a entidade antes de salvar
            Set<ConstraintViolation<Aluno>> violations = validator.validate(aluno);
            if (!violations.isEmpty()) {
                String errorMsg = violations.iterator().next().getMessage();
                throw new IllegalArgumentException(errorMsg);
            }
            
            return alunoRepository.save(aluno);
        } else if (role == RoleEnum.PROFESSOR || role == RoleEnum.COORDENADOR) {
            Professor professor = new Professor();
            professor.setNome(usuario.getNome());
            professor.setEmail(usuario.getEmail());
            professor.setTelefone(usuario.getTelefone());
            professor.setSenha(usuario.getSenha());
            professor.setRole(role);
            professor.setMatricula(matricula);
            professor.setEhCoordenador(role == RoleEnum.COORDENADOR);
            
            // Valida a entidade antes de salvar
            Set<ConstraintViolation<Professor>> violations = validator.validate(professor);
            if (!violations.isEmpty()) {
                String errorMsg = violations.iterator().next().getMessage();
                throw new IllegalArgumentException(errorMsg);
            }
            
            return professorRepository.save(professor);
        } else {
            return usuarioRepository.save(usuario);
        }
    }

    @Transactional
    public Usuario atualizar(Long id, Usuario usuarioAtualizado, String matricula) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setEmail(usuarioAtualizado.getEmail());
            usuario.setTelefone(usuarioAtualizado.getTelefone());
            
            if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
                usuario.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
            }
            
            if (usuario instanceof Aluno) {
                ((Aluno) usuario).setMatricula(matricula);
                
                // Valida a entidade antes de salvar
                Set<ConstraintViolation<Aluno>> violations = validator.validate((Aluno) usuario);
                if (!violations.isEmpty()) {
                    String errorMsg = violations.iterator().next().getMessage();
                    throw new IllegalArgumentException(errorMsg);
                }
            } else if (usuario instanceof Professor) {
                ((Professor) usuario).setMatricula(matricula);
                ((Professor) usuario).setEhCoordenador(usuarioAtualizado.getRole() == RoleEnum.COORDENADOR);
                
                // Valida a entidade antes de salvar
                Set<ConstraintViolation<Professor>> violations = validator.validate((Professor) usuario);
                if (!violations.isEmpty()) {
                    String errorMsg = violations.iterator().next().getMessage();
                    throw new IllegalArgumentException(errorMsg);
                }
            }
            
            usuario.setRole(usuarioAtualizado.getRole());
            return usuarioRepository.save(usuario);
        }
        
        throw new RuntimeException("Usuário não encontrado com id: " + id);
    }

    @Transactional
    public void excluir(Long id) {
        usuarioRepository.deleteById(id);
    }

    public boolean emailExiste(String email) {
        return usuarioRepository.findByEmail(email) != null;
    }

    public boolean emailExiste(String email, Long excludeId) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        return usuario != null && !usuario.getId().equals(excludeId);
    }
}