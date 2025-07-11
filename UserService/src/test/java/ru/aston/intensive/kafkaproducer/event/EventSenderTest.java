package ru.aston.intensive.kafkaproducer.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.MimeTypeUtils;
import ru.aston.intensive.common.dto.UserNotificationDto;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

/**
 * Юнит-тесты для класса EventSender, ответственного за отправку сообщений в Kafka.
 */
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "/application-test.yaml")
class EventSenderTest {

    @Mock
    private KafkaTemplate<String, UserNotificationDto> kafkaTemplate;

    @InjectMocks
    private EventSender eventSender;

    private UserNotificationDto userNotificationDto;


    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        userNotificationDto = new UserNotificationDto();
        userNotificationDto.setEmail("test@example.com");
        userNotificationDto.setEventType("CREATED");

        Field topicNameField = EventSender.class.getDeclaredField("topicName");
        topicNameField.setAccessible(true);
        topicNameField.set(eventSender, "user-event");

        Field partitionField = EventSender.class.getDeclaredField("partition");
        partitionField.setAccessible(true);
        partitionField.setInt(eventSender, 1);

    }


    /**
     * Проверяет отправку сообщения в корректный топик и партицию.
     */
    @Test
    @DisplayName("Отправка сообщения в корректный топик и партицию")
    void sendMessage_shouldSendMessageToCorrectTopicAndPartition() {
        ArgumentCaptor<Message<UserNotificationDto>> messageCaptor = ArgumentCaptor.forClass(Message.class);

        eventSender.sendMessage(userNotificationDto);

        verify(kafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<UserNotificationDto> capturedMessage = messageCaptor.getValue();
        MessageHeaders headers = capturedMessage.getHeaders();

        assertEquals("user-event", headers.get(KafkaHeaders.TOPIC));
        assertEquals(1, headers.get(KafkaHeaders.PARTITION));
        assertEquals(MimeTypeUtils.APPLICATION_JSON, headers.get(MessageHeaders.CONTENT_TYPE));
        assertEquals(userNotificationDto, capturedMessage.getPayload());
    }

    /**
     * Проверяет выброс исключения при отправке null DTO.
     */
    @Test
    @DisplayName("Отправка null DTO вызывает IllegalArgumentException")
    void sendMessage_withNullDto_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> eventSender.sendMessage(null));

        verify(kafkaTemplate, never()).send(any(Message.class));
    }

    /**
     * Проверяет отправку сообщения с другим топиком и партицией.
     */
    @Test
    @DisplayName("Отправка сообщения с кастомным топиком и партицией")
    void sendMessage_withDifferentTopicAndPartition_shouldSendCorrectMessage() throws NoSuchFieldException, IllegalAccessException {
        Field topicNameField = EventSender.class.getDeclaredField("topicName");
        topicNameField.setAccessible(true);
        topicNameField.set(eventSender, "custom-topic");

        Field partitionField = EventSender.class.getDeclaredField("partition");
        partitionField.setAccessible(true);
        partitionField.setInt(eventSender, 2);

        ArgumentCaptor<Message<UserNotificationDto>> messageCaptor = ArgumentCaptor.forClass(Message.class);

        eventSender.sendMessage(userNotificationDto);

        verify(kafkaTemplate, times(1)).send(messageCaptor.capture());
        Message<UserNotificationDto> capturedMessage = messageCaptor.getValue();
        MessageHeaders headers = capturedMessage.getHeaders();
        assertEquals("custom-topic", headers.get(KafkaHeaders.TOPIC));
        assertEquals(2, headers.get(KafkaHeaders.PARTITION));
        assertEquals(MimeTypeUtils.APPLICATION_JSON, headers.get(MessageHeaders.CONTENT_TYPE));
        assertEquals(userNotificationDto, capturedMessage.getPayload());
    }

    /**
     * Проверяет выброс исключения при null KafkaTemplate.
     */
    @Test
    @DisplayName("Отправка сообщения с null KafkaTemplate вызывает NullPointerException")
    void sendMessage_withNullKafkaTemplate_shouldThrowNullPointerException() throws NoSuchFieldException, IllegalAccessException {
        Field kafkaTemplateField = EventSender.class.getDeclaredField("kafkaTemplate");
        kafkaTemplateField.setAccessible(true);
        kafkaTemplateField.set(eventSender, null);

        assertThrows(NullPointerException.class, () -> eventSender.sendMessage(userNotificationDto));
        verify(kafkaTemplate, never()).send(any(Message.class));
    }
}