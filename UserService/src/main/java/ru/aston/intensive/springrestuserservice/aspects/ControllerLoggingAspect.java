package ru.aston.intensive.springrestuserservice.aspects;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.aston.intensive.springrestuserservice.dto.UserDto;

import java.util.List;

/**
 * Аспект для логирования операций REST-контроллера пользователей.
 */
@Aspect
@Component
public class ControllerLoggingAspect {

    /** Логгер для записи сообщений. */
    private static final Logger logger = LoggerFactory.getLogger(ControllerLoggingAspect.class);

    /**
     * Логирует вызов метода получения списка пользователей перед его выполнением.
     */
    @Before("execution(* ru.aston.intensive.springrestuserservice.controllers." +
            "UsersController.getUsers(..))")
    public void logBeforeGetUsers() {
        logger.info("Получен GET-запрос для получения списка всех пользователей");
    }

    /**
     * Логирует результат получения списка пользователей.
     *
     * @param result Список возвращённых пользователей
     */
    @AfterReturning(pointcut = "execution(* ru.aston.intensive.springrestuserservice.controllers." +
            "UsersController.getUsers(..))", returning = "result")
    public void logAfterGetUsers(List<UserDto> result) {
        logger.info("Возвращено {} пользователей", result.size());
    }

    /**
     * Логирует вызов метода получения пользователя по ID перед его выполнением.
     *
     * @param id Идентификатор пользователя
     */
    @Before("execution(* ru.aston.intensive.springrestuserservice.controllers." +
            "UsersController.getUser(..)) && args(id)")
    public void logBeforeGetUser(Long id) {
        logger.info("Получен GET-запрос для получения пользователя с ID: {}", id);
    }

    /**
     * Логирует результат получения пользователя по ID.
     *
     * @param result Возвращённый пользователь
     */
    @AfterReturning(pointcut = "execution(* ru.aston.intensive.springrestuserservice.controllers." +
            "UsersController.getUser(..))", returning = "result")
    public void logAfterGetUser(UserDto result) {
        logger.info("Пользователь с email {} успешно возвращен", result.getEmail());
    }

    /**
     * Логирует вызов метода создания пользователя перед его выполнением.
     *
     * @param userDto DTO пользователя
     */
    @Before("execution(* ru.aston.intensive.springrestuserservice.controllers." +
            "UsersController.create(..)) && args(userDto,..)")
    public void logBeforeCreateUser(UserDto userDto) {
        logger.info("Получен POST-запрос для создания пользователя с email: {}", userDto.getEmail());
    }

    /**
     * Логирует успешное создание пользователя.
     */
    @AfterReturning(pointcut = "execution(* ru.aston.intensive.springrestuserservice.controllers." +
            "UsersController.create(..))")
    public void logAfterCreateUser() {
        logger.info("Пользователь успешно создан");
    }

    /**
     * Логирует вызов метода обновления пользователя перед его выполнением.
     *
     * @param id Идентификатор пользователя
     * @param userDto DTO пользователя
     */
    @Before("execution(* ru.aston.intensive.springrestuserservice.controllers." +
            "UsersController.updateUser(..)) && args(id,userDto,..)")
    public void logBeforeUpdateUser(Long id, UserDto userDto) {
        logger.info("Получен PUT-запрос для обновления пользователя с ID: {}", id);
    }

    /**
     * Логирует результат обновления пользователя.
     *
     * @param result Обновлённый пользователь
     */
    @AfterReturning(pointcut = "execution(* ru.aston.intensive.springrestuserservice.controllers." +
            "UsersController.updateUser(..))", returning = "result")
    public void logAfterUpdateUser(UserDto result) {
        logger.info("Пользователь с email {} успешно обновлен", result.getEmail());
    }

    /**
     * Логирует вызов метода удаления пользователя перед его выполнением.
     *
     * @param id Идентификатор пользователя
     */
    @Before("execution(* ru.aston.intensive.springrestuserservice.controllers." +
            "UsersController.deleteUser(..)) && args(id)")
    public void logBeforeDeleteUser(Long id) {
        logger.info("Получен DELETE-запрос для удаления пользователя с ID: {}", id);
    }

    /**
     * Логирует успешное удаление пользователя.
     */
    @AfterReturning(pointcut = "execution(* ru.aston.intensive.springrestuserservice.controllers." +
            "UsersController.deleteUser(..))")
    public void logAfterDeleteUser() {
        logger.info("Пользователь успешно удален");
    }
}