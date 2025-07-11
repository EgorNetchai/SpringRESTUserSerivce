package ru.aston.intensive.kafkaproducer.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import ru.aston.intensive.common.dto.UserNotificationDto;

/**
 * Компонент для отправки сообщений в Kafka.
 */
@Component
public class EventSender {

    /** Название топика Kafka для отправки сообщений. */
    @Value("user-event")
    private String topicName;

    /** Номер партиции для отправки сообщений. */
    @Value("1")
    private int partition;

    /** Шаблон Kafka для отправки сообщений. */
    private KafkaTemplate<String, UserNotificationDto> kafkaTemplate;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param kafkaTemplate Шаблон Kafka для отправки сообщений
     */
    @Autowired
    EventSender(KafkaTemplate<String, UserNotificationDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Отправляет сообщение в Kafka.
     *
     * @param userNotificationDto DTO с информацией о пользователе
     */
    public void sendMessage(UserNotificationDto userNotificationDto) {
        Message<UserNotificationDto> message = MessageBuilder.withPayload(userNotificationDto)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .setHeader(KafkaHeaders.PARTITION, partition)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();

        kafkaTemplate.send(message);
    }
}
