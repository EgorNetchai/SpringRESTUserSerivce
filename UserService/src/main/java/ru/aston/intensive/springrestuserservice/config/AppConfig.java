package ru.aston.intensive.springrestuserservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация приложения для настройки бинов.
 */
@Configuration
public class AppConfig {

    /**
     * Создаёт бин ModelMapper для преобразования объектов.
     *
     * @return Экземпляр ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}