package ru.aston.intensive.springrestuserservice.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import ru.aston.intensive.springrestuserservice.advice.GlobalExceptionHandler;
import ru.aston.intensive.springrestuserservice.util.UserErrorResponse;
import ru.aston.intensive.springrestuserservice.util.UserNotCreatedException;
import ru.aston.intensive.springrestuserservice.util.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestPropertySource(locations = "/application-test.yaml")
public class GlobalExceptionHandlerTest {

    /**
     * Тест проверяет обработку исключения UserNotFoundException в GlobalExceptionHandler.
     */
    @Test
    @DisplayName("Обработка UserNotFoundException")
    void testHandleUserNotFoundException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        UserNotFoundException exception = new UserNotFoundException("Пользователь не найден");

        ResponseEntity<UserErrorResponse> response = handler.handleException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
        assertNotNull(response.getBody());

        assertEquals("Пользователь не найден", response.getBody().getMessage(),
                "Сообщение должно совпадать");

        assertNotNull(response.getBody().getTimestamp(), "Временная метка не должна быть null");
    }

    /**
     * Тест проверяет обработку UserNotFoundException с null сообщением.
     */
    @Test
    @DisplayName("Обработка UserNotFoundException с null сообщением")
    void testHandleUserNotFoundExceptionWithNullMessage() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        UserNotFoundException exception = new UserNotFoundException(null);

        ResponseEntity<UserErrorResponse> response = handler.handleException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Статус должен быть NOT_FOUND");
        assertNotNull(response.getBody());

        assertEquals("Пользователь с таким id не найден!",
                response.getBody().getMessage(), "Сообщение должно быть дефолтным");

        assertNotNull(response.getBody().getTimestamp(), "Временная метка не должна быть null");
    }

    /**
     * Тест проверяет обработку исключения UserNotCreatedException в GlobalExceptionHandler.
     */
    @Test
    @DisplayName("Обработка UserNotCreatedException")
    void testHandleUserNotCreatedException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        UserNotCreatedException exception = new UserNotCreatedException("Ошибка создания пользователя");

        ResponseEntity<UserErrorResponse> response = handler.handleException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Статус должен быть BAD_REQUEST");
        assertNotNull(response.getBody());

        assertEquals("Ошибка создания пользователя",
                response.getBody().getMessage(), "Сообщение должно совпадать");

        assertNotNull(response.getBody().getTimestamp(), "Временная метка не должна быть null");
    }

    /**
     * Тест проверяет обработку UserNotCreatedException с null сообщением.
     */
    @Test
    @DisplayName("Обработка UserNotCreatedException с null сообщением")
    void testHandleUserNotCreatedExceptionWithNullMessage() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        UserNotCreatedException exception = new UserNotCreatedException(null);

        ResponseEntity<UserErrorResponse> response = handler.handleException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Статус должен быть BAD_REQUEST");

        assertNotNull(response.getBody());
        assertNull(response.getBody().getMessage(), "Сообщение должно быть null");

        assertNotNull(response.getBody().getTimestamp(), "Временная метка не должна быть null");
    }

    /**
     * Тест проверяет обработку исключения IllegalArgumentException в GlobalExceptionHandler.
     */
    @Test
    @DisplayName("Обработка IllegalArgumentException")
    void testHandleIllegalArgumentException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        IllegalArgumentException exception = new IllegalArgumentException("Неверный аргумент");

        ResponseEntity<UserErrorResponse> response = handler.handleException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Статус должен быть BAD_REQUEST");
        assertNotNull(response.getBody());

        assertEquals("Неверный аргумент",
                response.getBody().getMessage(), "Сообщение должно совпадать");

        assertNotNull(response.getBody().getTimestamp(), "Временная метка не должна быть null");
    }

    /**
     * Тест проверяет обработку IllegalArgumentException с null сообщением.
     */
    @Test
    @DisplayName("Обработка IllegalArgumentException с null сообщением")
    void testHandleIllegalArgumentExceptionWithNullMessage() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        IllegalArgumentException exception = new IllegalArgumentException();

        ResponseEntity<UserErrorResponse> response = handler.handleException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Статус должен быть BAD_REQUEST");

        assertNotNull(response.getBody());
        assertNull(response.getBody().getMessage(), "Сообщение должно быть null");

        assertNotNull(response.getBody().getTimestamp(), "Временная метка не должна быть null");
    }
}