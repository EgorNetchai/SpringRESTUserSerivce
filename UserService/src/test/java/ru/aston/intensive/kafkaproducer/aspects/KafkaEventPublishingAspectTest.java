package ru.aston.intensive.kafkaproducer.aspects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import ru.aston.intensive.common.dto.UserNotificationDto;
import ru.aston.intensive.kafkaproducer.aspect.KafkaEventPublishingAspect;
import ru.aston.intensive.kafkaproducer.event.EventSender;
import ru.aston.intensive.springrestuserservice.models.UserEntity;
import ru.aston.intensive.springrestuserservice.services.MapperService;
import ru.aston.intensive.springrestuserservice.services.UsersServiceCrud;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Юнит-тесты для аспекта KafkaEventPublishingAspect, публикующего события в Kafka.
 */
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "/application-test.yaml")
class KafkaEventPublishingAspectTest {

    @Mock
    private EventSender eventSender;

    @Mock
    private UsersServiceCrud usersServiceCrud;

    @Mock
    private MapperService mapper;

    @InjectMocks
    private KafkaEventPublishingAspect kafkaEventPublishingAspect;

    private UserEntity userEntity;
    private UserNotificationDto userNotificationDto;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("test@example.com");

        userNotificationDto = new UserNotificationDto();
        userNotificationDto.setEmail("test@example.com");
    }

    /**
     * Проверяет публикацию события создания пользователя.
     */
    @Test
    @DisplayName("Публикация события создания пользователя")
    void publishUserCreatedEvent_shouldSendMessageWithCreatedEventType() {
        when(mapper.convertToUserNotificationDto(userEntity)).thenReturn(userNotificationDto);

        kafkaEventPublishingAspect.publishUserCreatedEvent(userEntity);

        verify(mapper, times(1)).convertToUserNotificationDto(userEntity);
        verify(eventSender, times(1)).sendMessage(userNotificationDto);
        assert userNotificationDto.getEventType().equals("CREATED");
    }

    /**
     * Проверяет публикацию события удаления пользователя.
     */
    @Test
    @DisplayName("Публикация события удаления пользователя")
    void publishUserDeletedEvent_shouldSendMessageWithDeletedEventType() {
        Long userId = 1L;
        when(usersServiceCrud.findOne(userId)).thenReturn(userEntity);
        when(mapper.convertToUserNotificationDto(userEntity)).thenReturn(userNotificationDto);

        kafkaEventPublishingAspect.publishUserDeletedEvent(userId);

        verify(usersServiceCrud, times(1)).findOne(userId);
        verify(mapper, times(1)).convertToUserNotificationDto(userEntity);
        verify(eventSender, times(1)).sendMessage(userNotificationDto);
        assert userNotificationDto.getEventType().equals("DELETED");
    }
}