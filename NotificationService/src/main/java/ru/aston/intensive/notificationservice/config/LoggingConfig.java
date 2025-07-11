package ru.aston.intensive.notificationservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.aston.intensive.notificationservice.aspects.ExceptionHandlerAspect;
import ru.aston.intensive.notificationservice.aspects.LoggingAspect;

@Configuration
public class LoggingConfig {

    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger(LoggingAspect.class);
    }

    @Bean
    public Logger exceptionHandlerAspectLogger() {
        return LoggerFactory.getLogger(ExceptionHandlerAspect.class);
    }
}