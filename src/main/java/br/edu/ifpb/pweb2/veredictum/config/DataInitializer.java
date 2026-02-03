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

            Aluno aluno4 = new Aluno();
            aluno4.setNome("Pedro Henrique");
            aluno4.setEmail("pedro.henrique@ifpb.edu.br");
            aluno4.setSenha(encoder.encode("aluno123"));
            aluno4.setRole(RoleEnum.ALUNO);
            aluno4.setTelefone("83985555555");
            aluno4.setMatricula("20221004");

            Aluno aluno5 = new Aluno();
            aluno5.setNome("Ana Paula");
            aluno5.setEmail("ana.paula@ifpb.edu.br");
            aluno5.setSenha(encoder.encode("aluno123"));
            aluno5.setRole(RoleEnum.ALUNO);
            aluno5.setTelefone("83984444444");
            aluno5.setMatricula("20221005");

            Professor coordenador = new Professor();
            coordenador.setNome("Prof. Coordenador SI");
            coordenador.setEmail("coordenador@ifpb.edu.br");
            coordenador.setSenha(encoder.encode("coordenador123"));
            coordenador.setRole(RoleEnum.COORDENADOR);
            coordenador.setTelefone("83977777777");
            coordenador.setMatricula("SIAPE001");
            coordenador.setEhCoordenador(true);
            coordenador.getColegiados().add(colegiado);

            Professor professor = new Professor();
            professor.setNome("Prof. João Membro");
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
            alunoRepository.save(aluno4);
            alunoRepository.save(aluno5);
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
            p2.setStatus(StatusProcessoEnum.CRIADO);
            p2.setAluno(aluno);
            p2.setNumero("2024/002");
            p2.setAssunto(a2);
            p2.setTextoRequerimento("Solicito trancamento da disciplina de Cálculo I.");
            p2.setColegiado(colegiado);

            Processo p3 = new Processo();
            p3.setDataCriacao(LocalDate.now().minusDays(10));
            p3.setStatus(StatusProcessoEnum.CRIADO);
            p3.setAluno(aluno);
            p3.setNumero("2024/003");
            p3.setAssunto(a3);
            p3.setTextoRequerimento("Solicito justificativa de faltas por motivo de saúde.");
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
            p5.setStatus(StatusProcessoEnum.CRIADO);
            p5.setAluno(aluno2);
            p5.setNumero("2024/005");
            p5.setAssunto(a5);
            p5.setTextoRequerimento("Solicito aproveitamento de estudos da disciplina Banco de Dados cursada em outra instituição.");
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
            p7.setStatus(StatusProcessoEnum.CRIADO);
            p7.setAluno(aluno3);
            p7.setNumero("2024/007");
            p7.setAssunto(a2);
            p7.setTextoRequerimento("Solicito trancamento da disciplina de Estruturas de Dados por motivos de saúde.");
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
            p9.setStatus(StatusProcessoEnum.CRIADO);
            p9.setAluno(aluno2);
            p9.setNumero("2024/009");
            p9.setAssunto(a4);
            p9.setTextoRequerimento("Solicito recurso da avaliação final de Redes de Computadores.");
            p9.setColegiado(colegiado2);

            Processo p10 = new Processo();
            p10.setDataCriacao(LocalDate.now().minusDays(3));
            p10.setStatus(StatusProcessoEnum.CRIADO);
            p10.setAluno(aluno3);
            p10.setNumero("2024/010");
            p10.setAssunto(a5);
            p10.setTextoRequerimento("Solicito aproveitamento de estudos da disciplina Sistemas Operacionais.");
            p10.setColegiado(colegiado);

            Processo p11 = new Processo();
            p11.setDataCriacao(LocalDate.now().minusDays(8));
            p11.setStatus(StatusProcessoEnum.CRIADO);
            p11.setAluno(aluno4);
            p11.setNumero("2024/011");
            p11.setAssunto(a1);
            p11.setTextoRequerimento("Solicito revisão da nota da prova de Matemática Discreta.");
            p11.setColegiado(colegiado);

            Processo p12 = new Processo();
            p12.setDataCriacao(LocalDate.now().minusDays(4));
            p12.setStatus(StatusProcessoEnum.CRIADO);
            p12.setAluno(aluno4);
            p12.setNumero("2024/012");
            p12.setAssunto(a3);
            p12.setTextoRequerimento("Solicito abono de faltas por motivo de luto familiar.");
            p12.setColegiado(colegiado2);

            Processo p13 = new Processo();
            p13.setDataCriacao(LocalDate.now().minusDays(6));
            p13.setStatus(StatusProcessoEnum.CRIADO);
            p13.setAluno(aluno5);
            p13.setNumero("2024/013");
            p13.setAssunto(a2);
            p13.setTextoRequerimento("Solicito trancamento de matrícula no semestre atual.");
            p13.setColegiado(colegiado);

            Processo p14 = new Processo();
            p14.setDataCriacao(LocalDate.now().minusDays(9));
            p14.setStatus(StatusProcessoEnum.CRIADO);
            p14.setAluno(aluno5);
            p14.setNumero("2024/014");
            p14.setAssunto(a4);
            p14.setTextoRequerimento("Solicito recurso contra nota final de Algoritmos e Programação.");
            p14.setColegiado(colegiado);

            Processo p15 = new Processo();
            p15.setDataCriacao(LocalDate.now().minusDays(11));
            p15.setStatus(StatusProcessoEnum.CRIADO);
            p15.setAluno(aluno);
            p15.setNumero("2024/015");
            p15.setAssunto(a5);
            p15.setTextoRequerimento("Solicito aproveitamento de estudos da disciplina Inglês Técnico.");
            p15.setColegiado(colegiado2);

            processoRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15));

            // ---------------------------
            // Criar Reuniões
            // ---------------------------
            
            // ---------------------------
            // Alterar status dos processos que entrarão em pauta e definir relator
            // ---------------------------
            p1.setStatus(StatusProcessoEnum.EM_PAUTA);
            p1.setRelator(professor);
            
            p2.setStatus(StatusProcessoEnum.EM_PAUTA);
            p2.setRelator(professor);
            
            p3.setStatus(StatusProcessoEnum.EM_PAUTA);
            p3.setRelator(professor);
            
            p8.setStatus(StatusProcessoEnum.EM_PAUTA);
            p8.setRelator(professor);
            
            p9.setStatus(StatusProcessoEnum.EM_PAUTA);
            p9.setRelator(professor);
            
            // Salvar as alterações de status e relator dos processos
            processoRepository.saveAll(List.of(p1, p2, p3, p8, p9));

            // ---------------------------
            // Criar Reuniões com processos já em pauta
            // ---------------------------
            
            // Reunião PROGRAMADA - Colegiado 1
            Reuniao r1 = new Reuniao();
            r1.setData(LocalDateTime.now().plusDays(3));
            r1.setStatus(StatusReuniao.PROGRAMADA);
            r1.setCoordenador(coordenador);
            r1.getMembros().add(professor);
            r1.getMembros().add(professor2);
            r1.getPauta().add(p1);
            r1.getPauta().add(p2);

            // Reunião PROGRAMADA - Colegiado 1
            Reuniao r2 = new Reuniao();
            r2.setData(LocalDateTime.now().minusHours(1));
            r2.setStatus(StatusReuniao.PROGRAMADA);
            r2.setCoordenador(coordenador);
            r2.getMembros().add(professor);
            r2.getMembros().add(professor3);
            r2.getPauta().add(p3);

            // Reunião PROGRAMADA - Colegiado 2
            Reuniao r4 = new Reuniao();
            r4.setData(LocalDateTime.now().plusDays(5));
            r4.setStatus(StatusReuniao.PROGRAMADA);
            r4.setCoordenador(coordenador2);
            r4.getMembros().add(professor4);
            r4.getMembros().add(professor3);
            r4.getPauta().add(p8);
            r4.getPauta().add(p9);

            reuniaoRepository.saveAll(List.of(r1, r2, r4));

            System.out.println("\n" + "=".repeat(80));
            System.out.println("                    VEREDICTUM - DADOS INICIAIS");
            System.out.println("=".repeat(80));
            System.out.println("\n✅ Dados iniciais criados com sucesso!\n");
            
            System.out.println("📊 ESTATÍSTICAS:");
            System.out.println("   • Colegiados: 2");
            System.out.println("   • Usuários: 11 (1 admin + 5 alunos + 5 professores)");
            System.out.println("   • Assuntos: 5");
            System.out.println("   • Processos: 15 (10 com status CRIADO, 5 EM_PAUTA)");
            System.out.println("   • Reuniões: 3\n");
            
            System.out.println("=".repeat(80));
            System.out.println("🔐 CREDENCIAIS DE ACESSO");
            System.out.println("=".repeat(80));
            
            System.out.println("\n👤 ADMINISTRADOR:");
            System.out.println("   └─ admin@ifpb.edu.br / admin123\n");
            
            System.out.println("🎓 ALUNOS:");
            System.out.println("   ├─ aluno@ifpb.edu.br / aluno123 (Aluno Teste - Mat: 20221001)");
            System.out.println("   ├─ maria.silva@ifpb.edu.br / aluno123 (Maria Silva - Mat: 20221002)");
            System.out.println("   ├─ joao.santos@ifpb.edu.br / aluno123 (João Santos - Mat: 20221003)");
            System.out.println("   ├─ pedro.henrique@ifpb.edu.br / aluno123 (Pedro Henrique - Mat: 20221004)");
            System.out.println("   └─ ana.paula@ifpb.edu.br / aluno123 (Ana Paula - Mat: 20221005)\n");
            
            System.out.println("👔 COORDENADORES:");
            System.out.println("   ├─ coordenador@ifpb.edu.br / coordenador123");
            System.out.println("   │  └─ Prof. Coordenador SI (SIAPE: SIAPE001) - Colegiado de SI");
            System.out.println("   └─ roberto.lima@ifpb.edu.br / coordenador123");
            System.out.println("      └─ Prof. Roberto Lima (SIAPE: SIAPE005) - Colegiado de ADS\n");
            
            System.out.println("👨‍🏫 PROFESSORES:");
            System.out.println("   ├─ professor@ifpb.edu.br / professor123");
            System.out.println("   │  └─ Prof. João Membro (SIAPE: SIAPE002) - Colegiado de SI");
            System.out.println("   ├─ ana.costa@ifpb.edu.br / professor123");
            System.out.println("   │  └─ Prof. Ana Costa (SIAPE: SIAPE003) - Colegiado de SI");
            System.out.println("   ├─ carlos.mendes@ifpb.edu.br / professor123");
            System.out.println("   │  └─ Prof. Carlos Mendes (SIAPE: SIAPE004) - Colegiados de SI e ADS");
            System.out.println("   └─ paula.oliveira@ifpb.edu.br / professor123");
            System.out.println("      └─ Prof. Paula Oliveira (SIAPE: SIAPE006) - Colegiado de ADS\n");
            
            System.out.println("=".repeat(80));
            System.out.println("\n✨ Sistema pronto para uso!\n");
        };
    }
}
