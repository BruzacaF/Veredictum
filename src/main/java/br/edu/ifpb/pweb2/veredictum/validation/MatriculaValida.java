package br.edu.ifpb.pweb2.veredictum.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação customizada para validar matrícula de alunos e professores.
 * A matrícula deve seguir o formato: YYYYNNNN (ano com 4 dígitos + número sequencial com 4 dígitos)
 * Exemplo: 20221001, 20231234
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MatriculaValidaValidator.class)
public @interface MatriculaValida {
    String message() default "Matrícula inválida. O formato deve ser YYYYNNNN (ex: 20221001)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Define o ano mínimo aceito para a matrícula
     */
    int anoMinimo() default 2000;
    
    /**
     * Define o ano máximo aceito para a matrícula (0 = ano atual + 1)
     */
    int anoMaximo() default 0;
}
