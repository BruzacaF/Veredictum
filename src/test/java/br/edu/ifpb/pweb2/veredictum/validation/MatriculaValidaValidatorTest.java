package br.edu.ifpb.pweb2.veredictum.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o validador customizado de matrícula.
 * Verifica todas as regras de validação definidas na anotação @MatriculaValida.
 */
@DisplayName("Testes do Validador de Matrícula")
class MatriculaValidaValidatorTest {

    private Validator validator;

    // Classe auxiliar para testar a validação
    static class TesteMatricula {
        @MatriculaValida
        private String matricula;

        public TesteMatricula(String matricula) {
            this.matricula = matricula;
        }
    }

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve aceitar matrícula válida no formato correto")
    void deveAceitarMatriculaValida() {
        TesteMatricula teste = new TesteMatricula("20221001");
        Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
        assertTrue(violations.isEmpty(), "Matrícula válida não deveria gerar violações");
    }

    @Test
    @DisplayName("Deve aceitar matrícula com ano atual")
    void deveAceitarMatriculaAnoAtual() {
        int anoAtual = java.time.Year.now().getValue();
        TesteMatricula teste = new TesteMatricula(anoAtual + "0001");
        Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
        assertTrue(violations.isEmpty(), "Matrícula com ano atual não deveria gerar violações");
    }

    @Test
    @DisplayName("Deve aceitar matrícula com ano atual + 1")
    void deveAceitarMatriculaAnoProximo() {
        int anoProximo = java.time.Year.now().getValue() + 1;
        TesteMatricula teste = new TesteMatricula(anoProximo + "0001");
        Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
        assertTrue(violations.isEmpty(), "Matrícula com ano atual+1 não deveria gerar violações");
    }

    @Test
    @DisplayName("Deve aceitar matrícula vazia (validação opcional)")
    void deveAceitarMatriculaVazia() {
        TesteMatricula teste = new TesteMatricula("");
        Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
        assertTrue(violations.isEmpty(), "Matrícula vazia não deveria gerar violações");
    }

    @Test
    @DisplayName("Deve aceitar matrícula nula (validação opcional)")
    void deveAceitarMatriculaNula() {
        TesteMatricula teste = new TesteMatricula(null);
        Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
        assertTrue(violations.isEmpty(), "Matrícula nula não deveria gerar violações");
    }

    @Test
    @DisplayName("Deve rejeitar matrícula com menos de 8 dígitos")
    void deveRejeitarMatriculaCurta() {
        TesteMatricula teste = new TesteMatricula("2022001");
        Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
        assertFalse(violations.isEmpty(), "Matrícula com 7 dígitos deveria gerar violação");
        assertTrue(violations.iterator().next().getMessage().contains("8 dígitos"),
                "Mensagem deveria mencionar 8 dígitos");
    }

    @Test
    @DisplayName("Deve rejeitar matrícula com mais de 8 dígitos")
    void deveRejeitarMatriculaLonga() {
        TesteMatricula teste = new TesteMatricula("202210011");
        Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
        assertFalse(violations.isEmpty(), "Matrícula com 9 dígitos deveria gerar violação");
    }

    @Test
    @DisplayName("Deve rejeitar matrícula com caracteres não numéricos")
    void deveRejeitarMatriculaComLetras() {
        TesteMatricula teste = new TesteMatricula("2022ABC1");
        Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
        assertFalse(violations.isEmpty(), "Matrícula com letras deveria gerar violação");
    }

    @Test
    @DisplayName("Deve rejeitar matrícula com ano inferior ao mínimo (2000)")
    void deveRejeitarMatriculaAnoInvalido() {
        TesteMatricula teste = new TesteMatricula("19991001");
        Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
        assertFalse(violations.isEmpty(), "Matrícula com ano < 2000 deveria gerar violação");
        assertTrue(violations.iterator().next().getMessage().contains("ano"),
                "Mensagem deveria mencionar o ano");
    }

    @Test
    @DisplayName("Deve rejeitar matrícula com ano futuro além do permitido")
    void deveRejeitarMatriculaAnoFuturo() {
        int anoFuturo = java.time.Year.now().getValue() + 2;
        TesteMatricula teste = new TesteMatricula(anoFuturo + "0001");
        Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
        assertFalse(violations.isEmpty(), "Matrícula com ano muito no futuro deveria gerar violação");
    }

    @Test
    @DisplayName("Deve rejeitar matrícula com número sequencial zero")
    void deveRejeitarMatriculaNumeroZero() {
        TesteMatricula teste = new TesteMatricula("20220000");
        Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
        assertFalse(violations.isEmpty(), "Matrícula com número sequencial 0000 deveria gerar violação");
        assertTrue(violations.iterator().next().getMessage().contains("sequencial"),
                "Mensagem deveria mencionar número sequencial");
    }

    @Test
    @DisplayName("Deve aceitar matrícula com número sequencial 0001")
    void deveAceitarMatriculaNumero0001() {
        TesteMatricula teste = new TesteMatricula("20220001");
        Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
        assertTrue(violations.isEmpty(), "Matrícula com número 0001 não deveria gerar violações");
    }

    @Test
    @DisplayName("Deve aceitar matrícula com número sequencial 9999")
    void deveAceitarMatriculaNumero9999() {
        TesteMatricula teste = new TesteMatricula("20229999");
        Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
        assertTrue(violations.isEmpty(), "Matrícula com número 9999 não deveria gerar violações");
    }

    @Test
    @DisplayName("Deve aceitar matrícula com espaços que serão removidos")
    void deveAceitarMatriculaComEspacos() {
        TesteMatricula teste = new TesteMatricula("  20221001  ");
        Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
        assertTrue(violations.isEmpty(), "Matrícula com espaços deveria ser aceita após trim");
    }

    @Test
    @DisplayName("Deve validar múltiplas matrículas conhecidas como válidas")
    void deveValidarMatriculasConhecidas() {
        String[] matriculasValidas = {"20221001", "20221002", "20221003", "20241234", "20005678"};
        
        for (String matricula : matriculasValidas) {
            TesteMatricula teste = new TesteMatricula(matricula);
            Set<ConstraintViolation<TesteMatricula>> violations = validator.validate(teste);
            assertTrue(violations.isEmpty(), 
                    "Matrícula " + matricula + " deveria ser válida");
        }
    }
}
