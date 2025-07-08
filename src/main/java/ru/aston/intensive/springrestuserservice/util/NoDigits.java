package ru.aston.intensive.springrestuserservice.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для проверки отсутствия цифр в строке.
 * Применяется к полям или методам, содержащим текст.
 */
@Constraint(validatedBy = NoDigitsValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoDigits {
    /**
     * Сообщение об ошибке, отображаемое при наличии цифр в строке.
     *
     * @return сообщение об ошибке
     */
    String message() default "Имя не должно содержать цифры";

    /**
     * Группы валидации.
     *
     * @return массив групп валидации
     */
    Class<?>[] groups() default {};

    /**
     * Полезная нагрузка для валидации.
     *
     * @return массив классов полезной нагрузки
     */
    Class<? extends Payload>[] payload() default {};
}