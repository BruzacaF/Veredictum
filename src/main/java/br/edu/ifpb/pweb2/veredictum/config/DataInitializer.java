package br.edu.ifpb.pweb2.veredictum.config;

import br.edu.ifpb.pweb2.veredictum.enums.RoleEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusProcessoEnum;
import br.edu.ifpb.pweb2.veredictum.enums.StatusReuniao;
import br.edu.ifpb.pweb2.veredictum.model.*;
import br.edu.ifpb.pweb2.veredictum.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository,
                                   AlunoRepository alunoRepository,
                                   ProfessorRepository professorRepository,
                                   AssuntoRepository assuntoRepository,
                                   ProcessoRepository processoRepository,
                                   ColegiadoRepository colegiadoRepository,
                                   ReuniaoRepository reuniaoRepository) {

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

            Colegiado colegiado2 = new Colegiado();
            colegiado2.setDescricao("Colegiado de Análise e Desenvolvimento de Sistemas");
            colegiado2.setPortaria("Portaria nº 002/2024");
            colegiado2.setDataInicio(LocalDate.of(2024, 1, 1));
            colegiado2.setDataFim(LocalDate.of(2025, 12, 31));
            colegiadoRepository.save(colegiado2);

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

            Aluno aluno2 = new Aluno();
            aluno2.setNome("Maria Silva");
            aluno2.setEmail("maria.silva@ifpb.edu.br");
            aluno2.setSenha(encoder.encode("aluno123"));
            aluno2.setRole(RoleEnum.ALUNO);
            aluno2.setTelefone("83987777777");
            aluno2.setMatricula("20221002");

            Aluno aluno3 = new Aluno();
            aluno3.setNome("João Santos");
            aluno3.setEmail("joao.santos@ifpb.edu.br");
            aluno3.setSenha(encoder.encode("aluno123"));
            aluno3.setRole(RoleEnum.ALUNO);
            aluno3.setTelefone("83986666666");
            aluno3.setMatricula("20221003");

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

            Professor professor2 = new Professor();
            professor2.setNome("Prof. Ana Costa");
            professor2.setEmail("ana.costa@ifpb.edu.br");
            professor2.setSenha(encoder.encode("professor123"));
            professor2.setRole(RoleEnum.PROFESSOR);
            professor2.setTelefone("83965555555");
            professor2.setMatricula("SIAPE003");
            professor2.setEhCoordenador(false);
            professor2.getColegiados().add(colegiado);

            Professor professor3 = new Professor();
            professor3.setNome("Prof. Carlos Mendes");
            professor3.setEmail("carlos.mendes@ifpb.edu.br");
            professor3.setSenha(encoder.encode("professor123"));
            professor3.setRole(RoleEnum.PROFESSOR);
            professor3.setTelefone("83964444444");
            professor3.setMatricula("SIAPE004");
            professor3.setEhCoordenador(false);
            professor3.getColegiados().add(colegiado);
            professor3.getColegiados().add(colegiado2);

            Professor coordenador2 = new Professor();
            coordenador2.setNome("Prof. Roberto Lima");
            coordenador2.setEmail("roberto.lima@ifpb.edu.br");
            coordenador2.setSenha(encoder.encode("coordenador123"));
            coordenador2.setRole(RoleEnum.COORDENADOR);
            coordenador2.setTelefone("83963333333");
            coordenador2.setMatricula("SIAPE005");
            coordenador2.setEhCoordenador(true);
            coordenador2.getColegiados().add(colegiado2);

            Professor professor4 = new Professor();
            professor4.setNome("Prof. Paula Oliveira");
            professor4.setEmail("paula.oliveira@ifpb.edu.br");
            professor4.setSenha(encoder.encode("professor123"));
            professor4.setRole(RoleEnum.PROFESSOR);
            professor4.setTelefone("83962222222");
            professor4.setMatricula("SIAPE006");
            professor4.setEhCoordenador(false);
            professor4.getColegiados().add(colegiado2);

            usuarioRepository.save(admin);
            alunoRepository.save(aluno);
            alunoRepository.save(aluno2);
            alunoRepository.save(aluno3);
            professorRepository.save(coordenador);
            professorRepository.save(professor);
            professorRepository.save(professor2);
            professorRepository.save(professor3);
            professorRepository.save(coordenador2);
            professorRepository.save(professor4);

            // ---------------------------
            // Criar Assuntos
            // ---------------------------
            Assunto a1 = new Assunto();
            a1.setNome("Revisão de Nota");

            Assunto a2 = new Assunto();
            a2.setNome("Trancamento de Disciplina");

            Assunto a3 = new Assunto();
            a3.setNome("Problemas de Frequência");

            Assunto a4 = new Assunto();
            a4.setNome("Recurso de Avaliação");

            Assunto a5 = new Assunto();
            a5.setNome("Aproveitamento de Estudos");

            assuntoRepository.save(a1);
            assuntoRepository.save(a2);
            assuntoRepository.save(a3);
            assuntoRepository.save(a4);
            assuntoRepository.save(a5);

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

            Processo p4 = new Processo();
            p4.setDataCriacao(LocalDate.now().minusDays(2));
            p4.setStatus(StatusProcessoEnum.CRIADO);
            p4.setAluno(aluno2);
            p4.setNumero("2024/004");
            p4.setAssunto(a4);
            p4.setTextoRequerimento("Solicito recurso contra nota da segunda unidade de Programação Orientada a Objetos.");
            p4.setColegiado(colegiado);

            Processo p5 = new Processo();
            p5.setDataCriacao(LocalDate.now().minusDays(7));
            p5.setStatus(StatusProcessoEnum.DISTRIBUIDO);
            p5.setAluno(aluno2);
            p5.setNumero("2024/005");
            p5.setAssunto(a5);
            p5.setTextoRequerimento("Solicito aproveitamento de estudos da disciplina Banco de Dados cursada em outra instituição.");
            p5.setRelator(professor2);
            p5.setColegiado(colegiado);

            Processo p6 = new Processo();
            p6.setDataCriacao(LocalDate.now().minusDays(15));
            p6.setStatus(StatusProcessoEnum.CRIADO);
            p6.setAluno(aluno3);
            p6.setNumero("2024/006");
            p6.setAssunto(a1);
            p6.setTextoRequerimento("Solicito revisão da nota da atividade prática de Engenharia de Software.");
            p6.setColegiado(colegiado);

            Processo p7 = new Processo();
            p7.setDataCriacao(LocalDate.now().minusDays(12));
            p7.setStatus(StatusProcessoEnum.DISTRIBUIDO);
            p7.setAluno(aluno3);
            p7.setNumero("2024/007");
            p7.setAssunto(a2);
            p7.setTextoRequerimento("Solicito trancamento da disciplina de Estruturas de Dados por motivos de saúde.");
            p7.setRelator(professor3);
            p7.setColegiado(colegiado);

            Processo p8 = new Processo();
            p8.setDataCriacao(LocalDate.now().minusDays(1));
            p8.setStatus(StatusProcessoEnum.CRIADO);
            p8.setAluno(aluno);
            p8.setNumero("2024/008");
            p8.setAssunto(a3);
            p8.setTextoRequerimento("Solicito abono de faltas no período de 10 a 20 de novembro devido internamento hospitalar.");
            p8.setColegiado(colegiado2);

            Processo p9 = new Processo();
            p9.setDataCriacao(LocalDate.now().minusDays(20));
            p9.setStatus(StatusProcessoEnum.DISTRIBUIDO);
            p9.setAluno(aluno2);
            p9.setNumero("2024/009");
            p9.setAssunto(a4);
            p9.setTextoRequerimento("Solicito recurso da avaliação final de Redes de Computadores.");
            p9.setRelator(professor4);
            p9.setColegiado(colegiado2);

            processoRepository.save(p1);
            processoRepository.save(p2);
            processoRepository.save(p3);
            processoRepository.save(p4);
            processoRepository.save(p5);
            processoRepository.save(p6);
            processoRepository.save(p7);
            processoRepository.save(p8);
            processoRepository.save(p9);

            // ---------------------------
            // Criar Reuniões
            // ---------------------------
            
            // Reunião PROGRAMADA - Colegiado 1
            Reuniao r1 = new Reuniao();
            r1.setData(LocalDateTime.now().plusDays(3));
            r1.setStatus(StatusReuniao.PROGRAMADA);
            r1.setCoordenador(coordenador);
            r1.getMembros().add(coordenador);
            r1.getMembros().add(professor);
            r1.getMembros().add(professor2);
            r1.getPauta().add(p1);
            r1.getPauta().add(p2);

            // Reunião PROGRAMADA - Colegiado 1
            Reuniao r2 = new Reuniao();
            r2.setData(LocalDateTime.now().minusHours(1));
            r2.setStatus(StatusReuniao.PROGRAMADA);
            r2.setCoordenador(coordenador);
            r2.getMembros().add(coordenador);
            r2.getMembros().add(professor);
            r2.getMembros().add(professor3);
            r2.getPauta().add(p3);

            // Reunião ENCERRADA - Colegiado 1
            Reuniao r3 = new Reuniao();
            r3.setData(LocalDateTime.now().minusDays(7));
            r3.setStatus(StatusReuniao.ENCERRADA);
            r3.setCoordenador(coordenador);
            r3.getMembros().add(coordenador);
            r3.getMembros().add(professor);
            r3.getMembros().add(professor2);

            // Reunião PROGRAMADA - Colegiado 2
            Reuniao r4 = new Reuniao();
            r4.setData(LocalDateTime.now().plusDays(5));
            r4.setStatus(StatusReuniao.PROGRAMADA);
            r4.setCoordenador(coordenador2);
            r4.getMembros().add(coordenador2);
            r4.getMembros().add(professor4);
            r4.getMembros().add(professor3);
            r4.getPauta().add(p8);
            r4.getPauta().add(p9);

            // Reunião EM_ANDAMENTO - Colegiado 1
            Reuniao r5 = new Reuniao();
            r5.setData(LocalDateTime.now().minusDays(2));
            r5.setStatus(StatusReuniao.EM_ANDAMENTO);
            r5.setCoordenador(coordenador);
            r5.getMembros().add(coordenador);
            r5.getMembros().add(professor);

            reuniaoRepository.saveAll(List.of(r1, r2, r3, r4, r5));

            System.out.println("✔ Dados iniciais criados com sucesso!");
            System.out.println("📧 Logins disponíveis:");
            System.out.println("   Admin: admin@ifpb.edu.br / admin123");
            System.out.println("   Alunos:");
            System.out.println("      - aluno@ifpb.edu.br / aluno123");
            System.out.println("      - maria.silva@ifpb.edu.br / aluno123");
            System.out.println("      - joao.santos@ifpb.edu.br / aluno123");
            System.out.println("   Coordenadores:");
            System.out.println("      - coordenador@ifpb.edu.br / coordenador123 (Colegiado SI)");
            System.out.println("      - roberto.lima@ifpb.edu.br / coordenador123 (Colegiado ADS)");
            System.out.println("   Professores:");
            System.out.println("      - professor@ifpb.edu.br / professor123");
            System.out.println("      - ana.costa@ifpb.edu.br / professor123");
            System.out.println("      - carlos.mendes@ifpb.edu.br / professor123");
            System.out.println("      - paula.oliveira@ifpb.edu.br / professor123");
        };
    }
}
