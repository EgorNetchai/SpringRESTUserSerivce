package ru.aston.intensive.springrestuserservice.services;

import java.util.List;
import ru.aston.intensive.springrestuserservice.models.UserEntity;

/**
 * Интерфейс для CRUD-операций с пользователями.
 */
public interface UsersServiceCrud {

    /**
     * Возвращает список всех пользователей.
     *
     * @return Список сущностей пользователей
     */
    List<UserEntity> findAll();

    /**
     * Находит пользователя по идентификатору.
     *
     * @param id Идентификатор пользователя
     *
     * @return Сущность пользователя
     */
    UserEntity findOne(Long id);

    /**
     * Сохраняет нового пользователя.
     *
     * @param userEntity Сущность пользователя
     */
    void save(UserEntity userEntity);

    /**
     * Обновляет данные пользователя.
     *
     * @param id Идентификатор пользователя
     * @param updatedUserEntity Обновлённая сущность пользователя
     *
     * @return Обновлённая сущность пользователя
     */
    UserEntity update(Long id, UserEntity updatedUserEntity);

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id Идентификатор пользователя
     */
    void delete(Long id);
}