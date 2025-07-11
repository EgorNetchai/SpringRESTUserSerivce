package ru.aston.intensive.notificationservice.aspects;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;
import ru.aston.intensive.common.dto.UserNotificationDto;

/**
 * Аспект для обработки исключений, возникающих в сервисе отправки email.
 */
@Aspect
@Component
public class ExceptionHandlerAspect {

    private final Logger logger;

    @Autowired
    public ExceptionHandlerAspect(Logger logger) {
        this.logger = logger;
    }

    /**
     * Обрабатывает исключения, возникшие при отправке email.
     *
     * @param event событие пользователя
     * @param ex исключение
     */
    @AfterThrowing(pointcut = "execution(* ru.aston.intensive.notificationservice.services." +
            "EmailService.sendEmail(..)) && args(event)", throwing = "ex")
    public void handleSendEmailException(UserNotificationDto event, Exception ex) {
        if (ex instanceof MailException) {
            logger.error("Не удалось отправить email на адрес {}: {}", event.getEmail(), ex.getMessage(), ex);
        } else {
            logger.error("Непредвиденная ошибка при отправке email на адрес {}: {}", event.getEmail(), ex.getMessage(), ex);
        }
    }

    /**
     * Обрабатывает исключения, возникшие при обработке события пользователя.
     *
     * @param event событие пользователя
     * @param ex исключение
     */
    @AfterThrowing(pointcut = "execution(* ru.aston.intensive.notificationservice.services." +
            "EmailService.listenUserEvents(..)) && args(event)", throwing = "ex")
    public void handleListenUserEventsException(UserNotificationDto event, Exception ex) {
        logger.error("Ошибка обработки события пользователя для email {}: {}",
                event != null ? event.getEmail() : null, ex.getMessage(), ex);
    }
}