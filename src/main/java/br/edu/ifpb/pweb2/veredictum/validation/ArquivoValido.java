package br.edu.ifpb.pweb2.veredictum.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ArquivoValidoValidator.class)
public @interface ArquivoValido {
    String message() default "Arquivo inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
