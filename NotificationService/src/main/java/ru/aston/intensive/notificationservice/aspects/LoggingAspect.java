package ru.aston.intensive.notificationservice.aspects;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Aspect;
import ru.aston.intensive.common.dto.UserNotificationDto;
/**
 * Аспект для логирования операций сервиса отправки email.
 */
@Aspect
@Component
public class LoggingAspect {

    private final Logger logger;

    @Autowired
    public LoggingAspect(Logger logger) {
        this.logger = logger;
    }

    /**
     * Логирует информацию о полученном событии пользователя перед его обработкой.
     *
     * @param event событие пользователя
     */
    @Before("execution(* ru.aston.intensive.notificationservice.services." +
            "EmailService.listenUserEvents(..)) && args(event)")
    public void logBeforeListenUserEvents(UserNotificationDto event) {
        logger.info("Получено событие пользователя: email={}, тип события={}",
                event != null ? event.getEmail() : null,
                event != null ? event.getEventType() : null);
    }

    /**
     * Логирует информацию об успешной отправке email.
     *
     * @param event событие пользователя
     */
    @AfterReturning("execution(* ru.aston.intensive.notificationservice.services." +
            "EmailService.sendEmail(..)) && args(event)")
    public void logAfterSendEmail(UserNotificationDto event) {
        logger.info("Email успешно отправлен на адрес {}", event.getEmail());
    }
}