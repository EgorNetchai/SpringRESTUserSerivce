package ru.aston.intensive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Главный класс приложения Spring REST UserEntity Service.
 * Отвечает за запуск приложения и конфигурацию компонентов.
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SpringRestUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRestUserServiceApplication.class, args);
	}

}
