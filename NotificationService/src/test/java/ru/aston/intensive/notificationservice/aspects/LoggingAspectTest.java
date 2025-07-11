package ru.aston.intensive.notificationservice.aspects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import ru.aston.intensive.common.dto.UserNotificationDto;

import static org.mockito.Mockito.verify;

/**
 * Юнит-тесты для аспекта логирования операций сервиса отправки email.
 */
@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @Mock
    private Logger logger;

    @InjectMocks
    private LoggingAspect loggingAspect;

    /**
     * Тестирует логирование перед обработкой события пользователя.
     */
    @Test
    @DisplayName("Логирование перед обработкой события пользователя")
    void testLogBeforeListenUserEvents() {
        // Arrange
        UserNotificationDto event = new UserNotificationDto();
        event.setEmail("test@example.com");
        event.setEventType("CREATED");

        loggingAspect.logBeforeListenUserEvents(event);

        verify(logger).info("Получено событие пользователя: email={}, тип события={}",
                event.getEmail(), event.getEventType());
    }

    /**
     * Тестирует логирование после успешной отправки email.
     */
    @Test
    @DisplayName("Логирование после успешной отправки email")
    void testLogAfterSendEmail() {
        UserNotificationDto event = new UserNotificationDto();
        event.setEmail("test@example.com");

        loggingAspect.logAfterSendEmail(event);

        verify(logger).info("Email успешно отправлен на адрес {}", event.getEmail());
    }
}