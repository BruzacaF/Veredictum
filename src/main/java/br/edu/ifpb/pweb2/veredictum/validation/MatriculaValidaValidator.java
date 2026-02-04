package br.edu.ifpb.pweb2.veredictum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;
import java.util.regex.Pattern;

/**
 * Validador customizado para matrícula de alunos e professores.
 * Regras de validação:
 * 1. A matrícula deve ter exatamente 8 dígitos numéricos
 * 2. Os primeiros 4 dígitos representam o ano de ingresso (YYYY)
 * 3. Os últimos 4 dígitos representam o número sequencial (NNNN)
 * 4. O ano deve estar entre o ano mínimo configurado e o ano atual + 1
 * 5. O número sequencial deve estar entre 0001 e 9999
 */
public class MatriculaValidaValidator implements ConstraintValidator<MatriculaValida, String> {

    private static final Pattern MATRICULA_PATTERN = Pattern.compile("^\\d{8}$");
    private int anoMinimo;
    private int anoMaximo;

    @Override
    public void initialize(MatriculaValida constraintAnnotation) {
        this.anoMinimo = constraintAnnotation.anoMinimo();
        this.anoMaximo = constraintAnnotation.anoMaximo();
        
        // Se anoMaximo não foi definido (0), usa o ano atual + 1
        if (this.anoMaximo == 0) {
            this.anoMaximo = Year.now().getValue() + 1;
        }
    }

    @Override
    public boolean isValid(String matricula, ConstraintValidatorContext context) {
        // Permite valores nulos ou vazios (use @NotNull ou @NotBlank para obrigatoriedade)
        if (matricula == null || matricula.trim().isEmpty()) {
            return true;
        }

        // Remove espaços em branco
        matricula = matricula.trim();

        // Verifica se a matrícula tem exatamente 8 dígitos numéricos
        if (!MATRICULA_PATTERN.matcher(matricula).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "A matrícula deve conter exatamente 8 dígitos numéricos"
            ).addConstraintViolation();
            return false;
        }

        // Extrai o ano e o número sequencial
        int ano = Integer.parseInt(matricula.substring(0, 4));
        int numeroSequencial = Integer.parseInt(matricula.substring(4, 8));

        // Valida o ano
        if (ano < anoMinimo || ano > anoMaximo) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("O ano da matrícula deve estar entre %d e %d", anoMinimo, anoMaximo)
            ).addConstraintViolation();
            return false;
        }

        // Valida o número sequencial (deve ser entre 1 e 9999)
        if (numeroSequencial < 1 || numeroSequencial > 9999) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "O número sequencial da matrícula deve estar entre 0001 e 9999"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
