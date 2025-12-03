package br.edu.ifpb.pweb2.veredictum.config;

import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.model.*;
import br.edu.ifpb.pweb2.veredictum.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository,
                                   AlunoRepository alunoRepository,
                                   ProfessorRepository professorRepository,
                                   AssuntoRepository assuntoRepository,
                                   ProcessoRepository processoRepository,
                                   ColegiadoRepository colegiadoRepository) {

        return args -> {

            if (usuarioRepository.count() != 0) return;

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // ---------------------------
            // Criar Colegiado
            // ---------------------------
            Colegiado colegiado = new Colegiado();
            colegiado.setDescricao("Colegiado de Sistemas de Informação");
            colegiado.setPortaria("Portaria nº 001/2024");
            colegiado.setDataInicio(LocalDate.of(2024, 1, 1));
            colegiado.setDataFim(LocalDate.of(2025, 12, 31));
            colegiadoRepository.save(colegiado);

            // ---------------------------
            // Criar Usuários
            // ---------------------------
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@ifpb.edu.br");
            admin.setSenha(encoder.encode("admin123"));
            admin.setRole(RoleEnum.ADMIN);
            admin.setTelefone("83999999999");

            Aluno aluno = new Aluno();
            aluno.setNome("Aluno Teste");
            aluno.setEmail("aluno@ifpb.edu.br");
            aluno.setSenha(encoder.encode("aluno123"));
            aluno.setRole(RoleEnum.ALUNO);
            aluno.setTelefone("83988888888");
            aluno.setMatricula("20221001");

            Professor coordenador = new Professor();
            coordenador.setNome("Prof. Coordenador");
            coordenador.setEmail("coordenador@ifpb.edu.br");
            coordenador.setSenha(encoder.encode("coordenador123"));
            coordenador.setRole(RoleEnum.COORDENADOR);
            coordenador.setTelefone("83977777777");
            coordenador.setMatricula("SIAPE001");
            coordenador.setEhCoordenador(true);
            coordenador.getColegiados().add(colegiado);

            Professor professor = new Professor();
            professor.setNome("Prof. Membro");
            professor.setEmail("professor@ifpb.edu.br");
            professor.setSenha(encoder.encode("professor123"));
            professor.setRole(RoleEnum.PROFESSOR);
            professor.setTelefone("83966666666");
            professor.setMatricula("SIAPE002");
            professor.setEhCoordenador(false);
            professor.getColegiados().add(colegiado);

            usuarioRepository.save(admin);
            alunoRepository.save(aluno);
            professorRepository.save(coordenador);
            professorRepository.save(professor);

            // ---------------------------
            // Criar Assuntos
            // ---------------------------
            Assunto a1 = new Assunto();
            a1.setNome("Revisão de Nota");

            Assunto a2 = new Assunto();
            a2.setNome("Trancamento de Disciplina");

            Assunto a3 = new Assunto();
            a3.setNome("Problemas de Frequência");

            assuntoRepository.save(a1);
            assuntoRepository.save(a2);
            assuntoRepository.save(a3);

            // ---------------------------
            // Criar Processos
            // ---------------------------
            Processo p1 = new Processo();
            p1.setDataCriacao(LocalDate.now());
            p1.setStatus(StatusProcessoEnum.CRIADO);
            p1.setAluno(aluno);
            p1.setNumero("2024/001");
            p1.setAssunto(a1);
            p1.setTextoRequerimento("Solicito revisão da nota da prova final.");
            p1.setColegiado(colegiado);

            Processo p2 = new Processo();
            p2.setDataCriacao(LocalDate.now().minusDays(5));
            p2.setStatus(StatusProcessoEnum.DISTRIBUIDO);
            p2.setAluno(aluno);
            p2.setNumero("2024/002");
            p2.setAssunto(a2);
            p2.setTextoRequerimento("Solicito trancamento da disciplina de Cálculo I.");
            p2.setRelator(professor);
            p2.setColegiado(colegiado);

            Processo p3 = new Processo();
            p3.setDataCriacao(LocalDate.now().minusDays(10));
            p3.setStatus(StatusProcessoEnum.EM_PAUTA);
            p3.setAluno(aluno);
            p3.setNumero("2024/003");
            p3.setAssunto(a3);
            p3.setTextoRequerimento("Solicito justificativa de faltas por motivo de saúde.");
            p3.setRelator(coordenador);
            p3.setColegiado(colegiado);

            processoRepository.save(p1);
            processoRepository.save(p2);
            processoRepository.save(p3);

            System.out.println("✔ Dados iniciais criados com sucesso!");
            System.out.println("📧 Logins disponíveis:");
            System.out.println("   Admin: admin@ifpb.edu.br / admin123");
            System.out.println("   Aluno: aluno@ifpb.edu.br / aluno123");
            System.out.println("   Coordenador: coordenador@ifpb.edu.br / coordenador123");
            System.out.println("   Professor: professor@ifpb.edu.br / professor123");
        };
    }
}
