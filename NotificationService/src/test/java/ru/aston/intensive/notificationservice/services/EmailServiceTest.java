package ru.aston.intensive.notificationservice.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.aston.intensive.common.dto.UserNotificationDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

/**
 * Юнит-тесты для сервиса отправки email-уведомлений.
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    /**
     * Тестирует отправку email для события создания пользователя.
     */
    @Test
    @DisplayName("Отправка email при создании пользователя")
    void testSendEmailForUserCreated() {
        UserNotificationDto event = new UserNotificationDto();
        event.setEmail("test@example.com");
        event.setEventType("CREATED");

        emailService.sendEmail(event);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    /**
     * Тестирует отправку email для события удаления пользователя.
     */
    @Test
    @DisplayName("Отправка email при удалении пользователя")
    void testSendEmailForUserDeleted() {
        UserNotificationDto event = new UserNotificationDto();
        event.setEmail("test@example.com");
        event.setEventType("DELETED");

        emailService.sendEmail(event);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}