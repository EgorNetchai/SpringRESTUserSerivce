package ru.aston.intensive.springrestuserservice.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.intensive.springrestuserservice.models.UserEntity;
import ru.aston.intensive.springrestuserservice.repositories.UsersRepository;
import ru.aston.intensive.springrestuserservice.util.UserNotFoundException;


/**
 * Сервис для управления пользователями.
 * Предоставляет методы для выполнения CRUD-операций над пользователями.
 */
@Service
@Transactional
public class UsersServiceCrudImpl implements UsersServiceCrud {

    private final UsersRepository usersRepository;


    /**
     * Конструктор сервиса пользователей.
     *
     * @param usersRepository репозиторий для работы с пользователями
     */
    @Autowired
    public UsersServiceCrudImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список пользователей
     *
     * @throws UserNotFoundException если список пользователей пуст
     */
    public List<UserEntity> findAll() {
        List<UserEntity> userEntities = usersRepository.findAll();

        if (userEntities.isEmpty()) {
            throw new UserNotFoundException();
        }

        return userEntities;
    }

    /**
     * Находит пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     *
     * @return найденный пользователь
     *
     * @throws UserNotFoundException если пользователь не найден
     */
    public UserEntity findOne(Long id) {
        Optional<UserEntity> foundUser = usersRepository.findById(id);

        return foundUser.orElseThrow(UserNotFoundException::new);
    }

    /**
     * Сохраняет нового пользователя.
     * Проверяет уникальность email и устанавливает временные метки.
     *
     * @param userEntity пользователь для сохранения
     *
     * @throws IllegalArgumentException если email уже занят
     */
    public void save(UserEntity userEntity) {

        if (usersRepository.existsByEmail(userEntity.getEmail())) {
            throw new IllegalArgumentException("Email уже занят");
        }

        userEntity.setCreated_at(LocalDateTime.now());
        userEntity.setUpdated_at(LocalDateTime.now());

        usersRepository.save(userEntity);
    }

    /**
     * Обновляет данные существующего пользователя.
     * Проверяет уникальность email и сохраняет временные метки.
     *
     * @param id          идентификатор пользователя
     * @param updatedUserEntity обновленные данные пользователя
     *
     * @return обновленный пользователь
     *
     * @throws UserNotFoundException   если пользователь не найден
     * @throws IllegalArgumentException если email уже занят
     */
    public UserEntity update(Long id, UserEntity updatedUserEntity) {
        UserEntity existingUserEntity = usersRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (!existingUserEntity.getEmail().equals(updatedUserEntity.getEmail()) &&
                usersRepository.existsByEmail(updatedUserEntity.getEmail())) {
            throw new IllegalArgumentException("Email уже занят");
        }

        existingUserEntity.setName(updatedUserEntity.getName());
        existingUserEntity.setEmail(updatedUserEntity.getEmail());
        existingUserEntity.setAge(updatedUserEntity.getAge());
        existingUserEntity.setUpdated_at(LocalDateTime.now());

        return usersRepository.save(existingUserEntity);
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     *
     * @throws UserNotFoundException если пользователь не найден
     */
    public void delete(Long id) {
        if (!usersRepository.existsById(id)) {
            throw new UserNotFoundException();
        }

        usersRepository.deleteById(id);
    }
}
