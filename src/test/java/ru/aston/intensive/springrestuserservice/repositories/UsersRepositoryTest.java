package ru.aston.intensive.springrestuserservice.repositories;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.intensive.springrestuserservice.models.UserEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестовый класс для проверки репозитория {@link UsersRepository}.
 * Проверяет операции сохранения, поиска, проверки существования и удаления
 * сущностей {@link UserEntity} в базе данных PostgreSQL с использованием Testcontainers.
 */

@DataJpaTest
@Testcontainers
@TestPropertySource(locations = "/application-test.yaml")
public class UsersRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private UsersRepository usersRepository;

    /**
     * Очищает базу данных перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();
    }

    /**
     * Проверяет сохранение и поиск пользователя с обычными данными.
     */
    @Test
    @DisplayName("Проверка сохранения и поиска пользователя по ID")
    void testSaveAndFindById() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        UserEntity savedUser = usersRepository.save(user);

        assertNotNull(savedUser.getId(), "Идентификатор сохраненного пользователя не должен быть null");

        var foundUser = usersRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent(), "Пользователь должен быть найден по ID");
        assertEquals("John Doe", foundUser.get().getName(), "Имя должно совпадать");
        assertEquals("john@example.com", foundUser.get().getEmail(), "Email должен совпадать");
        assertEquals(30, foundUser.get().getAge(), "Возраст должен совпадать");
    }

    /**
     * Проверяет сохранение и поиск пользователя с максимальной длиной имени и email.
     */
    @Test
    @DisplayName("Проверка сохранения пользователя с длинными строками")
    void testSaveAndFindWithLongStrings() {
        String longName = "a".repeat(50);
        String longEmail = "a".repeat(50) + "@example.com";
        UserEntity user = new UserEntity(longName, longEmail, 30);
        UserEntity savedUser = usersRepository.save(user);

        assertNotNull(savedUser.getId(), "Идентификатор сохраненного пользователя не должен быть null");

        var foundUser = usersRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent(), "Пользователь должен быть найден по ID");
        assertEquals(longName, foundUser.get().getName(), "Имя должно совпадать");
        assertEquals(longEmail, foundUser.get().getEmail(), "Email должен совпадать");
        assertEquals(30, foundUser.get().getAge(), "Возраст должен совпадать");
    }

    /**
     * Проверяет сохранение и поиск пользователя с граничными значениями возраста.
     */
    @Test
    @DisplayName("Проверка сохранения пользователя с граничными возрастами")
    void testSaveAndFindWithBoundaryAge() {
        UserEntity user = new UserEntity("Jane Smith", "jane@example.com", 150);
        UserEntity savedUser = usersRepository.save(user);

        assertNotNull(savedUser.getId(), "Идентификатор сохраненного пользователя не должен быть null");

        var foundUser = usersRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent(), "Пользователь должен быть найден по ID");
        assertEquals("Jane Smith", foundUser.get().getName(), "Имя должно совпадать");
        assertEquals("jane@example.com", foundUser.get().getEmail(), "Email должен совпадать");
        assertEquals(150, foundUser.get().getAge(), "Возраст должен совпадать");
    }

    /**
     * Проверяет, что сохранение пользователя с null значениями вызывает исключение.
     */
    @Test
    @DisplayName("Проверка ошибки при сохранении пользователя с null значениями")
    void testSaveWithNullValuesThrowsException() {
        UserEntity user = new UserEntity(null, null, 0);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> usersRepository.save(user),
                "Должно быть выброшено исключение ConstraintViolationException");

        assertTrue(exception.getMessage().contains("Имя не должно быть пустым"),
                "Сообщение об ошибке должно содержать 'Имя не должно быть пустым'");
        assertTrue(exception.getMessage().contains("Email не должен быть пустым"),
                "Сообщение об ошибке должно содержать 'Email не должен быть пустым'");
    }


    /**
     * Проверяет сохранение и извлечение всех пользователей.
     */
    @Test
    @DisplayName("Проверка сохранения и извлечения всех пользователей")
    void testFindAll() {
        UserEntity user1 = new UserEntity("John Doe", "john@example.com", 30);
        UserEntity user2 = new UserEntity("Jane Smith", "jane@example.com", 25);
        usersRepository.saveAll(List.of(user1, user2));

        List<UserEntity> users = usersRepository.findAll();

        assertEquals(2, users.size(), "Должно быть найдено 2 пользователя");

        assertTrue(users.stream().anyMatch(user -> user.getName().equals("John Doe") &&
                user.getEmail().equals("john@example.com") &&
                user.getAge() ==30), "Пользователь John Doe должен быть в списке");

        assertTrue(users.stream().anyMatch(user -> user.getName().equals("Jane Smith") &&
                user.getEmail().equals("jane@example.com") &&
                user.getAge() == 25), "Пользователь Jane Smith должен быть в списке");
    }

    /**
     * Проверяет метод existsByEmail для существующего и несуществующего email.
     */
    @Test
    @DisplayName("Проверка существования пользователя по email")
    void testExistsByEmail() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        usersRepository.save(user);

        assertTrue(usersRepository.existsByEmail("john@example.com"), "Пользователь с email 'john@example.com' должен существовать");
        assertFalse(usersRepository.existsByEmail("nonexistent@example.com"), "Пользователь с email 'nonexistent@example.com' не должен существовать");
    }

    /**
     * Проверяет удаление пользователя по идентификатору.
     */
    @Test
    @DisplayName("Проверка удаления пользователя по ID")
    void testDeleteById() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        UserEntity savedUser = usersRepository.save(user);

        usersRepository.deleteById(savedUser.getId());

        assertFalse(usersRepository.existsById(savedUser.getId()), "Пользователь с ID " + savedUser.getId() + " не должен существовать после удаления");
    }

    /**
     * Проверяет поиск несуществующего пользователя по ID.
     */
    @Test
    @DisplayName("Проверка поиска несуществующего пользователя")
    void testFindByNonExistentId() {
        var foundUser = usersRepository.findById(999L);

        assertFalse(foundUser.isPresent(), "Пользователь с несуществующим ID не должен быть найден");
    }

}