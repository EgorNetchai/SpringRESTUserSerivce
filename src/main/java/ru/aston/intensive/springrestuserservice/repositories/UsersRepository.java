package ru.aston.intensive.springrestuserservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.aston.intensive.springrestuserservice.models.UserEntity;

/**
 * Репозиторий для работы с пользователями.
 * Предоставляет методы для выполнения операций CRUD с сущностью {@link UserEntity}.
 */
@Repository
public interface UsersRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Проверяет, существует ли пользователь с указанным email.
     *
     * @param email Email пользователя, которого нужно проверить.
     *
     * @return true, если пользователь с таким email существует, иначе false.
     */
    boolean existsByEmail(String email);
}
