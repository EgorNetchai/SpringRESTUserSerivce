package ru.aston.intensive.kafkaproducer.kafkaconfig;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.aston.intensive.common.dto.UserNotificationDto;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация продюсера Kafka.
 */
@Configuration
public class ProducerKafkaConfig {

    /** Название топика Kafka. */
    @Value("user-event")
    private String topicName;

    /** Количество партиций для топика. */
    @Value("3")
    private int partitionNumber;

    /** Количество реплик для топика. */
    @Value("1")
    private int replicasNumber;

    /** Адрес сервера Kafka. */
    @Value("localhost:29092") //если брать из env падают тесты UsersServiceCrudImplIntegrationTest и UserControllerTest
    private String bootstrapAddress;


    /**
     * Создаёт топик Kafka.
     *
     * @return Объект топика с заданными параметрами
     */
    @Bean
    NewTopic createTopic() {
        return TopicBuilder.name(topicName)
                .partitions(partitionNumber)
                .replicas(replicasNumber)
                .build();
    }

    /**
     * Создаёт фабрику продюсера Kafka.
     *
     * @return Фабрика продюсера с настроенными сериализаторами
     */
    @Bean
    public ProducerFactory<String, UserNotificationDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Создаёт шаблон Kafka для отправки сообщений.
     *
     * @return Шаблон Kafka для работы с продюсером
     */
    @Bean
    public KafkaTemplate<String, UserNotificationDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
