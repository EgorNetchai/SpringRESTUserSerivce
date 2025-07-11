package ru.aston.intensive.springrestuserservice.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Валидатор для проверки отсутствия цифр в строке.
 * Использует регулярное выражение для проверки текста.
 */
public class NoDigitsValidator implements ConstraintValidator<NoDigits, String> {

    /**
     * Инициализирует валидатор.
     *
     * @param constraintAnnotation аннотация, содержащая параметры валидации
     */
    @Override
    public void initialize(NoDigits constraintAnnotation) {
    }

    /**
     * Проверяет, содержит ли строка только буквы, пробелы или дефисы.
     *
     * @param value   строка для проверки
     * @param context контекст валидации
     *
     * @return true, если строка не содержит цифр или null, false в противном случае
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // @NotEmpty проверит null отдельно
        }
        return value.matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$");
    }
}