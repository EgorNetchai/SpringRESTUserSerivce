package ru.aston.intensive.springrestuserservice.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для проверки корректности email-адреса.
 * Применяется к полям или методам, содержащим email.
 */
@Constraint(validatedBy = ValidEmailValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {

    /**
     * Сообщение об ошибке, отображаемое при некорректном email.
     *
     * @return сообщение об ошибке
     */
    String message() default "Email не должен содержать запрещенные символы и должен содержать точку в домене";

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
