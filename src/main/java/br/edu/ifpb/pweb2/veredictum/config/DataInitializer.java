package br.edu.ifpb.pweb2.veredictum.config;

import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.model.Assunto;
import br.edu.ifpb.pweb2.veredictum.model.Processo;
import br.edu.ifpb.pweb2.veredictum.model.Usuario;
import br.edu.ifpb.pweb2.veredictum.repository.AssuntoRepository;
import br.edu.ifpb.pweb2.veredictum.repository.ProcessoRepository;
import br.edu.ifpb.pweb2.veredictum.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository,
                                   AssuntoRepository assuntoRepository,
                                   ProcessoRepository processoRepository) {

        return args -> {

            if (usuarioRepository.count() != 0) return;

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // ---------------------------
            // Criar Usuários
            // ---------------------------
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@ifpb.edu.br");
            admin.setSenha(encoder.encode("admin123"));
            admin.setRole(RoleEnum.ADMIN);

            Usuario aluno = new Usuario();
            aluno.setNome("Aluno");
            aluno.setEmail("aluno@ifpb.edu.br");
            aluno.setSenha(encoder.encode("aluno123"));
            aluno.setRole(RoleEnum.ALUNO);

            Usuario coordenador = new Usuario();
            coordenador.setNome("Coordenador");
            coordenador.setEmail("coordenador@ifpb.edu.br");
            coordenador.setSenha(encoder.encode("coordenador123"));
            coordenador.setRole(RoleEnum.COORDENADOR);

            Usuario professor = new Usuario();
            professor.setNome("Professor");
            professor.setEmail("professor@ifpb.edu.br");
            professor.setSenha(encoder.encode("professor123"));
            professor.setRole(RoleEnum.PROFESSOR);

            usuarioRepository.save(admin);
            usuarioRepository.save(aluno);
            usuarioRepository.save(coordenador);
            usuarioRepository.save(professor);

            // ---------------------------
            // Criar Assuntos
            // ---------------------------
            Assunto a1 = new Assunto();
            a1.setDescricao("Revisão de Nota");

            Assunto a2 = new Assunto();
            a2.setDescricao("Trancamento de Disciplina");

            Assunto a3 = new Assunto();
            a3.setDescricao("Problemas de Frequência");

            assuntoRepository.save(a1);
            assuntoRepository.save(a2);
            assuntoRepository.save(a3);

            // ---------------------------
            // Criar Processos (do aluno)
            // ---------------------------
            Processo p1 = new Processo();
            p1.setAluno(aluno);
            p1.setAssunto(a1);
            p1.setTextoRequerimento("Gostaria de solicitar revisão da prova 2.");

            Processo p2 = new Processo();
            p2.setAluno(aluno);
            p2.setAssunto(a2);
            p2.setTextoRequerimento("Solicito trancamento da disciplina Programação Web.");

            Processo p3 = new Processo();
            p3.setAluno(aluno);
            p3.setAssunto(a3);
            p3.setTextoRequerimento("Problemas na contagem de faltas.");

            processoRepository.save(p1);
            processoRepository.save(p2);
            processoRepository.save(p3);

            System.out.println("✔ Dados iniciais criados com sucesso!");
        };
    }
}
