package ru.aston.intensive.kafkaproducer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.aston.intensive.common.dto.UserNotificationDto;
import ru.aston.intensive.kafkaproducer.aspect.KafkaEventPublishingAspect;
import ru.aston.intensive.springrestuserservice.models.UserEntity;
import ru.aston.intensive.springrestuserservice.services.MapperService;
import ru.aston.intensive.springrestuserservice.services.UsersServiceCrud;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Интеграционные тесты для проверки отправки сообщений в Kafka через аспект и продюсер.
 */
@DirtiesContext
@SpringBootTest(properties = {"spring.autoconfigure.exclude=" +
        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"})
@EmbeddedKafka(partitions = 3, controlledShutdown = true, topics = {"user-event"})
@TestPropertySource(locations = "/application-test.yaml")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(KafkaIntegrationTest.class);

    @Autowired
    private KafkaEventPublishingAspect eventAspect;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockitoBean
    private UsersServiceCrud usersServiceCrud;

    @MockitoBean
    private MapperService mapperService;

    private KafkaMessageListenerContainer<String, UserNotificationDto> kafkaListener;
    private BlockingQueue<ConsumerRecord<String, UserNotificationDto>> records;

    /**
     * Настраивает тестовую среду перед каждым тестом, включая создание и запуск Kafka-консюмера.
     */
    @BeforeEach
    void setUp() {
        DefaultKafkaConsumerFactory<String, UserNotificationDto> consumerFactory =
                new DefaultKafkaConsumerFactory<>(getConsumerProperties());
        ContainerProperties containerProperties = new ContainerProperties("user-event");
        kafkaListener = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        kafkaListener.setupMessageListener((MessageListener<String, UserNotificationDto>) records::add);
        kafkaListener.start();
        ContainerTestUtils.waitForAssignment(kafkaListener, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    /**
     * Останавливает контейнер после каждого теста для предотвращения утечек ресурсов.
     */
    @AfterEach
    void tearDown() {
        if (kafkaListener != null) {
            kafkaListener.stop();
        }
    }

    /**
     * Возвращает конфигурацию для Kafka-консюмера.
     *
     * @return Карта с настройками консюмера
     */
    private Map<String, Object> getConsumerProperties() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.GROUP_ID_CONFIG, "test-group",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                JsonDeserializer.TRUSTED_PACKAGES, "ru.aston.intensive.common.dto"
        );
    }

    /**
     * Проверяет отправку события создания пользователя в Kafka.
     *
     * @throws Exception если получение сообщения из Kafka не удалось
     */
    @Test
    @DisplayName("Проверка отправки события создания пользователя в Kafka")
    void testPublishUserCreatedEvent() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Test User");
        userEntity.setEmail("test@example.com");

        UserNotificationDto expectedDto = new UserNotificationDto();
        expectedDto.setEmail("test@example.com");
        expectedDto.setEventType("CREATED");

        doNothing().when(usersServiceCrud).save(userEntity);
        when(mapperService.convertToUserNotificationDto(userEntity)).thenReturn(expectedDto);

        eventAspect.publishUserCreatedEvent(userEntity);

        ConsumerRecord<String, UserNotificationDto> received = records.poll(3, TimeUnit.SECONDS);

        assertNotNull(received);
        assertNotNull(received.value());
        assertEquals(expectedDto.getEmail(), received.value().getEmail());
        assertEquals("CREATED", received.value().getEventType());
    }

    /**
     * Проверяет отправку события удаления пользователя в Kafka.
     *
     * @throws Exception если получение сообщения из Kafka не удалось
     */
    @Test
    @DisplayName("Проверка отправки события удаления пользователя в Kafka")
    void testPublishUserDeletedEvent() throws Exception {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setName("Test User");
        userEntity.setEmail("test@example.com");

        UserNotificationDto expectedDto = new UserNotificationDto();
        expectedDto.setEmail("test@example.com");
        expectedDto.setEventType("DELETED");

        when(usersServiceCrud.findOne(userId)).thenReturn(userEntity);
        when(mapperService.convertToUserNotificationDto(userEntity)).thenReturn(expectedDto);

        eventAspect.publishUserDeletedEvent(userId);

        ConsumerRecord<String, UserNotificationDto> received = records.poll(3, TimeUnit.SECONDS);

        assertNotNull(received, "Сообщение должно быть получено из Kafka");
        assertNotNull(received.value(), "Полученное сообщение не должно быть null");
        assertEquals(expectedDto.getEmail(), received.value().getEmail(), "Email пользователя должен совпадать");
        assertEquals("DELETED", received.value().getEventType(), "Тип события должен быть DELETED");
    }
}