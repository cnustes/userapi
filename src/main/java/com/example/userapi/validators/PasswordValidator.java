package com.example.userapi.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Value("${validation.password.regex}")
    private String passwordRegex;

    private Pattern pattern;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // Inicializa el patrón
        pattern = Pattern.compile(passwordRegex);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return pattern.matcher(value).matches();
    }
}