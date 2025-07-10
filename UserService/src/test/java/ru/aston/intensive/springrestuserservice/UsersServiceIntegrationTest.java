package ru.aston.intensive.springrestuserservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.intensive.springrestuserservice.models.User;
import ru.aston.intensive.springrestuserservice.services.UsersService;
import ru.aston.intensive.springrestuserservice.util.UserNotFoundException;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционные тесты для сервиса {@link UsersService}.
 * Используют Testcontainers для запуска PostgreSQL в Docker.
 */
@SpringBootTest
@Testcontainers
public class UsersServiceIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private UsersService usersService;


    /**
     * Настраивает свойства базы данных для тестов.
     * Использует параметры подключения, предоставленные Testcontainers.
     *
     * @param registry реестр для динамической настройки свойств Spring
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        try {
            List<User> users = usersService.findAll();
            users.forEach(user -> usersService.delete(user.getId()));
        } catch (UserNotFoundException e) {
            // Пустая база данных, ничего не делаем
        }
    }

    /**
     * Тестирует создание и получение пользователя.
     */
    @Test
    @DisplayName("Создание и поиск пользователя")
    void testCreateAndFindUser() {
        User user = new User("John Doe", "john@example.com", 30);
        usersService.save(user);

        User foundUser = usersService.findOne(user.getId());

        assertNotNull(foundUser);
        assertEquals("John Doe", foundUser.getName());
        assertEquals("john@example.com", foundUser.getEmail());
        assertEquals(30, foundUser.getAge());
    }

    /**
     * Тестирует получение списка пользователей.
     */
    @Test
    @DisplayName("Получение списка всех пользователей")
    void testFindAllUsers() {
        User user1 = new User("John Doe", "john@example.com", 30);
        User user2 = new User("Jane Doe", "jane@example.com", 25);
        usersService.save(user1);
        usersService.save(user2);

        List<User> users = usersService.findAll();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("john@example.com")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("jane@example.com")));
    }

    /**
     * Тестирует обновление пользователя.
     */
    @Test
    @DisplayName("Обновление данных пользователя")
    void testUpdateUser() {
        User user = new User("John Doe", "john@example.com", 30);
        usersService.save(user);

        User updatedUser = new User("John Smith", "john.smith@example.com", 31);
        User result = usersService.update(user.getId(), updatedUser);

        assertEquals("John Smith", result.getName());
        assertEquals("john.smith@example.com", result.getEmail());
        assertEquals(31, result.getAge());
        assertEquals(user.getCreated_at().truncatedTo(ChronoUnit.MICROS),
                result.getCreated_at().truncatedTo(ChronoUnit.MICROS));

        assertNotNull(result.getUpdated_at());

        User foundUser = usersService.findOne(user.getId());

        assertEquals("John Smith", foundUser.getName());
        assertEquals("john.smith@example.com", foundUser.getEmail());
        assertEquals(31, foundUser.getAge());
    }

    /**
     * Тестирует удаление пользователя.
     */
    @Test
    @DisplayName("Удаление пользователя")
    void testDeleteUser() {
        User user = new User("John Doe", "john@example.com", 30);
        usersService.save(user);

        usersService.delete(user.getId());

        assertThrows(UserNotFoundException.class, () -> usersService.findOne(user.getId()));
    }

    /**
     * Тестирует выброс исключения при попытке найти несуществующего пользователя.
     */
    @Test
    @DisplayName("Поиск несуществующего пользователя")
    void testFindNonExistentUser() {
        assertThrows(UserNotFoundException.class, () -> usersService.findOne(999L));
    }
}