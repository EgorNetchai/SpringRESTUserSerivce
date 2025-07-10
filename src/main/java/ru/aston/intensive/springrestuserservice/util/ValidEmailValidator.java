package ru.aston.intensive.springrestuserservice.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Валидатор для проверки корректности email-адреса.
 * Использует регулярное выражение для проверки формата email.
 */
public class ValidEmailValidator implements ConstraintValidator<ValidEmail, String> {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]*" +
            "[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?)+$";

    /**
     * Инициализирует валидатор.
     *
     * @param constraintAnnotation аннотация, содержащая параметры валидации
     */
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }

    /**
     * Проверяет, соответствует ли переданное значение формату email.
     *
     * @param value   строка, содержащая email-адрес
     * @param context контекст валидации
     *
     * @return true, если email корректен или null, false в противном случае
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.matches(EMAIL_REGEX);
    }
}