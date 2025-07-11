package ru.aston.intensive.springrestuserservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.intensive.springrestuserservice.models.UserEntity;
import ru.aston.intensive.springrestuserservice.util.UserNotFoundException;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционные тесты для сервиса {@link UsersServiceCrudImpl}.
 * Используют Testcontainers для запуска PostgreSQL в Docker.
 */

@SpringBootTest
@Testcontainers
@DirtiesContext
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
}) //Почему-то не исключает из контекста загрузку конфига кафки, из-за чего падает если значения берутся из окружения
// и ждет продюсера кафки, если она не запущена в контейнере
public class UsersServiceCrudImplIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");


    @Autowired
    private UsersServiceCrudImpl usersServiceImpl;


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
            List<UserEntity> userEntities = usersServiceImpl.findAll();
            userEntities.forEach(user -> usersServiceImpl.delete(user.getId()));
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
        UserEntity userEntity = new UserEntity("John Doe", "john@example.com", 30);
        usersServiceImpl.save(userEntity);

        UserEntity foundUserEntity = usersServiceImpl.findOne(userEntity.getId());

        assertNotNull(foundUserEntity);
        assertEquals("John Doe", foundUserEntity.getName());
        assertEquals("john@example.com", foundUserEntity.getEmail());
        assertEquals(30, foundUserEntity.getAge());
    }

    /**
     * Тестирует получение списка пользователей.
     */
    @Test
    @DisplayName("Получение списка всех пользователей")
    void testFindAllUsers() {
        UserEntity userEntity1 = new UserEntity("John Doe", "john@example.com", 30);
        UserEntity userEntity2 = new UserEntity("Jane Doe", "jane@example.com", 25);

        usersServiceImpl.save(userEntity1);
        usersServiceImpl.save(userEntity2);

        List<UserEntity> userEntities = usersServiceImpl.findAll();

        assertEquals(2, userEntities.size());
        assertTrue(userEntities.stream().anyMatch(u -> u.getEmail().equals("john@example.com")));
        assertTrue(userEntities.stream().anyMatch(u -> u.getEmail().equals("jane@example.com")));
    }

    /**
     * Тестирует обновление пользователя.
     */
    @Test
    @DisplayName("Обновление данных пользователя")
    void testUpdateUser() {
        UserEntity userEntity = new UserEntity("John Doe", "john@example.com", 30);

        usersServiceImpl.save(userEntity);

        UserEntity updatedUserEntity = new UserEntity("John Smith", "john.smith@example.com", 31);
        UserEntity result = usersServiceImpl.update(userEntity.getId(), updatedUserEntity);

        assertEquals("John Smith", result.getName());
        assertEquals("john.smith@example.com", result.getEmail());
        assertEquals(31, result.getAge());

        assertEquals(userEntity.getCreated_at().truncatedTo(ChronoUnit.SECONDS),
                result.getCreated_at().truncatedTo(ChronoUnit.SECONDS));

        assertNotNull(result.getUpdated_at());

        UserEntity foundUserEntity = usersServiceImpl.findOne(userEntity.getId());

        assertEquals("John Smith", foundUserEntity.getName());
        assertEquals("john.smith@example.com", foundUserEntity.getEmail());
        assertEquals(31, foundUserEntity.getAge());
    }

    /**
     * Тестирует удаление пользователя.
     */
    @Test
    @DisplayName("Удаление пользователя")
    void testDeleteUser() {
        UserEntity userEntity = new UserEntity("John Doe", "john@example.com", 30);

        usersServiceImpl.save(userEntity);

        usersServiceImpl.delete(userEntity.getId());

        assertThrows(UserNotFoundException.class, () -> usersServiceImpl.findOne(userEntity.getId()));
    }

    /**
     * Тестирует выброс исключения при попытке найти несуществующего пользователя.
     */
    @Test
    @DisplayName("Поиск несуществующего пользователя")
    void testFindNonExistentUser() {
        assertThrows(UserNotFoundException.class, () -> usersServiceImpl.findOne(999L));
    }
}