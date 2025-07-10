package ru.aston.intensive.springrestuserservice.aspects;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.aston.intensive.springrestuserservice.util.UserErrorResponse;
import ru.aston.intensive.springrestuserservice.util.UserNotCreatedException;

/**
 * Аспект для логирования ошибок, обработанных глобальным обработчиком исключений.
 */
@Aspect
@Component
public class ErrorLoggingAspect {

    /** Логгер для записи сообщений об ошибках. */
    private static final Logger logger = LoggerFactory.getLogger(ErrorLoggingAspect.class);

    /**
     * Логирует ошибки, обработанные глобальным обработчиком исключений.
     *
     * @param e Исключение, которое было обработано
     * @param result Ответ с описанием ошибки
     */
    @AfterReturning(
            pointcut = "execution(* ru.aston.intensive.springrestuserservice.controllers." +
                    "GlobalExceptionHandler.handleException(..)) && args(e)",
            returning = "result"
    )
    public void logError(Exception e, ResponseEntity<UserErrorResponse> result) {
        String message = getErrorMessage(result, e);
        if (e instanceof UserNotCreatedException) {
            logger.error("Ошибка создания/обновления пользователя: {}", message);
        } else {
            logger.error("Ошибка: {}", message);
        }
    }

    /**
     * Извлекает сообщение об ошибке из ответа или исключения.
     *
     * @param result Ответ с описанием ошибки
     * @param e Исключение
     *
     * @return Сообщение об ошибке
     */
    private String getErrorMessage(ResponseEntity<UserErrorResponse> result, Exception e) {
        if (result != null && result.getBody() != null && result.getBody().getMessage() != null) {
            return result.getBody().getMessage();
        }
        return e.getMessage() != null ? e.getMessage() : "Неизвестная ошибка";
    }
}