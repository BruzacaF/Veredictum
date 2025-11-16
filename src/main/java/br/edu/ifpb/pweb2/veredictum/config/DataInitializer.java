package br.edu.ifpb.pweb2.veredictum.config;

import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import br.edu.ifpb.pweb2.veredictum.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                Usuario admin = new Usuario();
                admin.setNome("Administrador");
                admin.setEmail("admin@ifpb.edu.br");
                admin.setSenha(encoder.encode("admin123"));
                admin.setRole(RoleEnum.ADMIN);

                Usuario aluno = new Usuario();
                aluno.setNome("Aluno");
                aluno.setEmail("Aluno@ifpb.edu.br");
                aluno.setSenha(encoder.encode("Aluno123"));
                aluno.setRole(RoleEnum.ALUNO);

                Usuario coordenador = new Usuario();
                coordenador.setNome("Coordenador");
                coordenador.setEmail("Coordenador@ifpb.edu.br");
                coordenador.setSenha(encoder.encode("Coordenador123"));
                coordenador.setRole(RoleEnum.COORDENADOR);

                Usuario usuario = new Usuario();
                usuario.setNome("Professor");
                usuario.setEmail("Professor@ifpb.edu.br");
                usuario.setSenha(encoder.encode("Professor123"));
                usuario.setRole(RoleEnum.PROFESSOR);

                usuarioRepository.save(admin);
                usuarioRepository.save(aluno);
                usuarioRepository.save(coordenador);
                usuarioRepository.save(usuario);

                System.out.println("Usuários iniciais criados com sucesso!");
            }
        };
    }
}