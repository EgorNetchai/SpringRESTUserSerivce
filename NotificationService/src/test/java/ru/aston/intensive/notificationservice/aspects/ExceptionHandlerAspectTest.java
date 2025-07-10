package ru.aston.intensive.notificationservice.aspects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.mail.MailException;
import ru.aston.intensive.common.dto.UserNotificationDto;

import static org.mockito.Mockito.verify;

/**
 * Юнит-тесты для аспекта обработки исключений.
 */
@ExtendWith(MockitoExtension.class)
class ExceptionHandlerAspectTest {

    @Mock
    private Logger logger;

    @InjectMocks
    private ExceptionHandlerAspect exceptionHandlerAspect;

    /**
     * Тестирует обработку MailException при отправке email.
     */
    @Test
    @DisplayName("Обработка MailException при отправке email")
    void testHandleSendEmailMailException() {
        UserNotificationDto event = new UserNotificationDto();
        event.setEmail("test@example.com");
        MailException exception = new MailException("Mail server error") {};

        exceptionHandlerAspect.handleSendEmailException(event, exception);

        verify(logger).error("Не удалось отправить email на адрес {}: {}",
                event.getEmail(), exception.getMessage(), exception);
    }

    /**
     * Тестирует обработку непредвиденной ошибки при отправке email.
     */
    @Test
    @DisplayName("Обработка непредвиденной ошибки при отправке email")
    void testHandleSendEmailUnexpectedException() {
        UserNotificationDto event = new UserNotificationDto();
        event.setEmail("test@example.com");
        Exception exception = new Exception("Unexpected error");

        exceptionHandlerAspect.handleSendEmailException(event, exception);

        verify(logger).error("Непредвиденная ошибка при отправке email на адрес {}: {}",
                event.getEmail(), exception.getMessage(), exception);
    }

    /**
     * Тестирует обработку исключения при обработке события пользователя.
     */
    @Test
    @DisplayName("Обработка исключения при обработке события пользователя")
    void testHandleListenUserEventsException() {
        UserNotificationDto event = new UserNotificationDto();
        event.setEmail("test@example.com");
        Exception exception = new Exception("Processing error");

        exceptionHandlerAspect.handleListenUserEventsException(event, exception);

        verify(logger).error("Ошибка обработки события пользователя для email {}: {}",
                event.getEmail(), exception.getMessage(), exception);
    }
}