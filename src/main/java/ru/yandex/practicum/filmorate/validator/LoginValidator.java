package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.ValidLogin;

import java.util.regex.Pattern;

public class LoginValidator implements ConstraintValidator<ValidLogin, String> {

    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && LOGIN_PATTERN.matcher(value).matches();
    }
}
