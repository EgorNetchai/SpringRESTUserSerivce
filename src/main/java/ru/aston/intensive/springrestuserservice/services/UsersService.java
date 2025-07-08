package ru.aston.intensive.springrestuserservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.intensive.springrestuserservice.models.User;
import ru.aston.intensive.springrestuserservice.repositories.UsersRepository;
import ru.aston.intensive.springrestuserservice.util.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления пользователями.
 * Предоставляет методы для выполнения CRUD-операций над пользователями.
 */
@Service
@Transactional
public class UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    private final UsersRepository usersRepository;

    /**
     * Конструктор сервиса пользователей.
     *
     * @param usersRepository репозиторий для работы с пользователями
     */
    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список пользователей
     *
     * @throws UserNotFoundException если список пользователей пуст
     */
    public List<User> findAll() {
        logger.info("Запрос списка всех пользователей");
        List<User> users = usersRepository.findAll();

        if (users.isEmpty()) {
            logger.error("Список пользователей пуст");
            throw new UserNotFoundException();
        }

        logger.info("Найдено {} пользователей", users.size());
        return users;
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
    public User findOne(Long id) {
        logger.info("Поиск пользователя с ID: {}", id);
        Optional<User> foundUser = usersRepository.findById(id);

        return foundUser.orElseThrow(() -> {
            logger.error("Пользователь с ID {} не найден", id);
            return new UserNotFoundException();
        });
    }

    /**
     * Сохраняет нового пользователя.
     * Проверяет уникальность email и устанавливает временные метки.
     *
     * @param user пользователь для сохранения
     *
     * @throws IllegalArgumentException если email уже занят
     */
    public void save(User user) {
        logger.info("Попытка сохранения пользователя с email: {}", user.getEmail());

        if (usersRepository.existsByEmail(user.getEmail())) {
            logger.error("Email {} уже занят", user.getEmail());
            throw new IllegalArgumentException("Email уже занят");
        }

        user.setCreated_at(LocalDateTime.now());
        user.setUpdated_at(LocalDateTime.now());

        usersRepository.save(user);
        logger.info("Пользователь с email {} успешно сохранен", user.getEmail());
    }

    /**
     * Обновляет данные существующего пользователя.
     * Проверяет уникальность email и сохраняет временные метки.
     *
     * @param id          идентификатор пользователя
     * @param updatedUser обновленные данные пользователя
     *
     * @return обновленный пользователь
     *
     * @throws UserNotFoundException   если пользователь не найден
     * @throws IllegalArgumentException если email уже занят
     */
    public User update(Long id, User updatedUser) {
        logger.info("Попытка обновления пользователя с ID: {}", id);
        User existingUser = usersRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден для обновления", id);
                    return new UserNotFoundException();
                });

        if (!existingUser.getEmail().equals(updatedUser.getEmail()) &&
                usersRepository.existsByEmail(updatedUser.getEmail())) {
            logger.error("Email {} уже занят для пользователя с ID {}", updatedUser.getEmail(), id);
            throw new IllegalArgumentException("Email уже занят");
        }

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setAge(updatedUser.getAge());
        existingUser.setUpdated_at(LocalDateTime.now());

        User savedUser = usersRepository.save(existingUser);
        logger.info("Пользователь с ID {} успешно обновлен", id);
        return savedUser;
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     *
     * @throws UserNotFoundException если пользователь не найден
     */
    public void delete(Long id) {
        logger.info("Попытка удаления пользователя с ID: {}", id);

        if (!usersRepository.existsById(id)) {
            logger.error("Пользователь с ID {} не найден для удаления", id);
            throw new UserNotFoundException();
        }

        usersRepository.deleteById(id);
        logger.info("Пользователь с ID {} успешно удален", id);
    }
}
