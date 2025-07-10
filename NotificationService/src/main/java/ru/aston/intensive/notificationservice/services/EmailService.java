package ru.aston.intensive.notificationservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.aston.intensive.common.dto.UserNotificationDto;

/**
 * Сервис для отправки email-уведомлений на основе событий пользователя, полученных из Kafka.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${MAIL_USERNAME}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    /**
     * Отправляет email на основе события пользователя.
     *
     * @param event событие пользователя
     */
    public void sendEmail(UserNotificationDto event) {
        String subject = event.getEventType().equals("CREATED")
                ? "Пользователь создан"
                : "Пользователь удален";
        String text = String.format("Пользователь с email %s %s.",
                event.getEmail(),
                event.getEventType().equals("CREATED") ? "создан" : "удален");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.getEmail());
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(fromEmail);
        mailSender.send(message);
    }
}