package ru.aston.intensive.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Основной класс приложения для сервиса уведомлений.
 * Запускает Spring Boot приложение и включает поддержку Kafka.
 */
@SpringBootApplication
@ComponentScan(basePackages = "ru.aston.intensive")
@EnableKafka
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}