package ru.aston.intensive.springrestuserservice.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.aston.intensive.springrestuserservice.util.UserErrorResponse;
import ru.aston.intensive.springrestuserservice.util.UserNotCreatedException;
import ru.aston.intensive.springrestuserservice.util.UserNotFoundException;

import java.time.LocalDateTime;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение, когда пользователь не найден.
     *
     * @param e Исключение UserNotFoundException
     *
     * @return Ответ с описанием ошибки и статусом 404
     */
    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handleException(UserNotFoundException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage() != null ? e.getMessage() : "Пользователь с таким id не найден!",
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Обрабатывает исключение, когда пользователь не создан или не обновлён.
     *
     * @param e Исключение UserNotCreatedException
     *
     * @return Ответ с описанием ошибки и статусом 400
     */
    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает исключение для некорректных аргументов.
     *
     * @param e Исключение IllegalArgumentException
     *
     * @return Ответ с описанием ошибки и статусом 400
     */
    @ExceptionHandler
    public ResponseEntity<UserErrorResponse> handleException(IllegalArgumentException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}