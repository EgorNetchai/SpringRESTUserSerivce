package ru.aston.intensive.springrestuserservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Главный класс приложения Spring REST User Service.
 * Отвечает за запуск приложения и конфигурацию компонентов.
 */
@SpringBootApplication
public class SpringRestUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRestUserServiceApplication.class, args);
	}

	/**
	 * Создает и возвращает бин ModelMapper для маппинга объектов.
	 *
	 * @return экземпляр ModelMapper
	 */
	@Bean
	public ModelMapper modelmapper() {
		return new ModelMapper();
	}
}
