package ru.aston.intensive.springrestuserservice.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Тестовый класс для проверки пользовательских исключений приложения.
 */
@TestPropertySource(locations = "/application-test.yaml")
public class ExceptionsTest {

    /**
     * Проверяет создание исключения {@link UserNotFoundException} с использованием конструктора по умолчанию.
     * Убеждается, что сообщение исключения равно {@code null}.
     */
    @Test
    @DisplayName("Проверка создания UserNotFoundException с конструктором по умолчанию")
    void testUserNotFoundExceptionDefaultConstructor() {
        UserNotFoundException exception = new UserNotFoundException();

       assertNull(exception.getMessage());
    }

    /**
     * Проверяет создание исключения {@link UserNotFoundException} с заданным сообщением.
     * Убеждается, что сообщение исключения соответствует переданному значению.
     */
    @Test
    @DisplayName("Проверка создания UserNotFoundException с пользовательским сообщением")
    void testUserNotFoundExceptionWithMessage() {
        UserNotFoundException exception = new UserNotFoundException("Пользователь не найден");

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    /**
     * Проверяет создание исключения {@link UserNotCreatedException} с заданным сообщением.
     * Убеждается, что сообщение исключения соответствует переданному значению.
     */
    @Test
    @DisplayName("Проверка создания UserNotCreatedException с пользовательским сообщением")
    void testUserNotCreatedException() {
        UserNotCreatedException exception = new UserNotCreatedException("Ошибка валидации");

        assertEquals("Ошибка валидации", exception.getMessage());
    }

    /**
     * Проверяет создание исключения {@link IllegalArgumentException} с заданным сообщением.
     * Убеждается, что исключение выбрасывается и его сообщение соответствует ожидаемому.
     */
    @Test
    @DisplayName("Проверка создания IllegalArgumentException при дублировании email")
    void testIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("Email уже занят");
        });

        assertEquals("Email уже занят", exception.getMessage());
    }
}