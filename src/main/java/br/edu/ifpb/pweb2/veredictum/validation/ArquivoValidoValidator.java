package br.edu.ifpb.pweb2.veredictum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ArquivoValidoValidator implements ConstraintValidator<ArquivoValido, MultipartFile> {

    private final Tika tika = new Tika();

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if (file == null || file.isEmpty()) {
            return true; // Se o arquivo for opcional
        }

        try {
            String mimeType = tika.detect(file.getInputStream());
            return mimeType.equals("application/pdf"); // ajuste para os tipos permitidos
        } catch (IOException e) {
            return false;
        }
    }
}
