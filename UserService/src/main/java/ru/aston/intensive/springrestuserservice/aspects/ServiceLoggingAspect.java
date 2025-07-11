package ru.aston.intensive.springrestuserservice.aspects;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.aston.intensive.springrestuserservice.models.UserEntity;

import java.util.List;

/**
 * Аспект для логирования операций сервиса пользователей.
 */
@Aspect
@Component
public class ServiceLoggingAspect {

    /** Логгер для записи сообщений. */
    private static final Logger logger = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    /**
     * Логирует вызов метода получения списка пользователей перед его выполнением.
     */
    @Before("execution(* ru.aston.intensive.springrestuserservice.services." +
            "UsersServiceCrudImpl.findAll(..))")
    public void logBeforeFindAll() {
        logger.info("Запрос списка всех пользователей");
    }

    /**
     * Логирует результат получения списка пользователей.
     *
     * @param result Список возвращённых пользователей
     */
    @AfterReturning(pointcut = "execution(* ru.aston.intensive.springrestuserservice.services." +
            "UsersServiceCrudImpl.findAll(..))", returning = "result")
    public void logAfterFindAll(List<UserEntity> result) {
        logger.info("Найдено {} пользователей", result.size());
    }

    /**
     * Логирует вызов метода получения пользователя по ID перед его выполнением.
     *
     * @param id Идентификатор пользователя
     */
    @Before("execution(* ru.aston.intensive.springrestuserservice.services." +
            "UsersServiceCrudImpl.findOne(..)) && args(id)")
    public void logBeforeFindOne(Long id) {
        logger.info("Поиск пользователя с ID: {}", id);
    }

    /**
     * Логирует результат получения пользователя по ID.
     *
     * @param result Возвращённый пользователь
     */
    @AfterReturning(pointcut = "execution(* ru.aston.intensive.springrestuserservice.services." +
            "UsersServiceCrudImpl.findOne(..))", returning = "result")
    public void logAfterFindOne(UserEntity result) {
        logger.info("Пользователь с email {} успешно найден", result.getEmail());
    }

    /**
     * Логирует вызов метода сохранения пользователя перед его выполнением.
     *
     * @param userEntity Сущность пользователя
     */
    @Before("execution(* ru.aston.intensive.springrestuserservice.services." +
            "UsersServiceCrudImpl.save(..)) && args(userEntity)")
    public void logBeforeSave(UserEntity userEntity) {
        logger.info("Попытка сохранения пользователя с email: {}", userEntity.getEmail());
    }

    /**
     * Логирует успешное сохранение пользователя.
     *
     * @param userEntity Сохранённый пользователь
     */
    @AfterReturning(pointcut = "execution(* ru.aston.intensive.springrestuserservice.services." +
            "UsersServiceCrudImpl.save(..)) && args(userEntity)")
    public void logAfterSave(UserEntity userEntity) {
        logger.info("Пользователь с email {} успешно сохранен", userEntity.getEmail());
    }

    /**
     * Логирует вызов метода обновления пользователя перед его выполнением.
     *
     * @param id Идентификатор пользователя
     * @param userEntity Сущность пользователя
     */
    @Before("execution(* ru.aston.intensive.springrestuserservice.services." +
            "UsersServiceCrudImpl.update(..)) && args(id,userEntity)")
    public void logBeforeUpdate(Long id, UserEntity userEntity) {
        logger.info("Попытка обновления пользователя с ID: {}", id);
    }

    /**
     * Логирует результат обновления пользователя.
     *
     * @param result Обновлённый пользователь
     */
    @AfterReturning(pointcut = "execution(* ru.aston.intensive.springrestuserservice.services." +
            "UsersServiceCrudImpl.update(..))", returning = "result")
    public void logAfterUpdate(UserEntity result) {
        logger.info("Пользователь с ID {} успешно обновлен", result.getId());
    }

    /**
     * Логирует вызов метода удаления пользователя перед его выполнением.
     *
     * @param id Идентификатор пользователя
     */
    @Before("execution(* ru.aston.intensive.springrestuserservice.services." +
            "UsersServiceCrudImpl.delete(..)) && args(id)")
    public void logBeforeDelete(Long id) {
        logger.info("Попытка удаления пользователя с ID: {}", id);
    }

    /**
     * Логирует успешное удаление пользователя.
     */
    @AfterReturning(pointcut = "execution(* ru.aston.intensive.springrestuserservice.services." +
            "UsersServiceCrudImpl.delete(..))")
    public void logAfterDelete() {
        logger.info("Пользователь успешно удален");
    }
}