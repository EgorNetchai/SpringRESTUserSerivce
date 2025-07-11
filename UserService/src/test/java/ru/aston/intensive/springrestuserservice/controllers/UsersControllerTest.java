package ru.aston.intensive.springrestuserservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.intensive.kafkaproducer.aspect.KafkaEventPublishingAspect;
import ru.aston.intensive.kafkaproducer.event.EventSender;
import ru.aston.intensive.kafkaproducer.kafkaconfig.ProducerKafkaConfig;
import ru.aston.intensive.springrestuserservice.dto.UserDto;
import ru.aston.intensive.springrestuserservice.models.UserEntity;
import ru.aston.intensive.springrestuserservice.services.UsersServiceCrudImpl;
import ru.aston.intensive.springrestuserservice.util.UserNotFoundException;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test .web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестовый класс для проверки REST-контроллера {@link UsersController}.
 * Использует {@link MockMvc} для эмуляции HTTP-запросов и проверяет корректность ответов.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties =
        {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"}
)//не исключает конфиг кафки, если какие-то переменные берутся из окружения, падает
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsersServiceCrudImpl usersServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Проверяет эндпоинт GET /users для получения списка всех пользователей.
     * Ожидает, что запрос возвращает статус 200 OK и список пользователей в формате JSON.
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    @Test
    @DisplayName("Получение списка всех пользователей")
    void testGetAllUsers() throws Exception {
        UserEntity userEntity = new UserEntity("John Doe", "john@example.com", 30);
        userEntity.setId(1L);
        List<UserEntity> userEntities = List.of(userEntity);

        when(usersServiceImpl.findAll()).thenReturn(userEntities);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[0].age").value(30));

        verify(usersServiceImpl, times(1)).findAll();
    }

    /**
     * Проверяет эндпоинт GET /users/{id} для получения пользователя по ID.
     * Ожидает, что запрос возвращает статус 200 OK и данные пользователя в формате JSON.
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    @Test
    @DisplayName("Получение пользователя по идентификатору")
    void testGetUserById() throws Exception {
        UserEntity userEntity = new UserEntity("John Doe", "john@example.com", 30);
        userEntity.setId(1L);

        when(usersServiceImpl.findOne(1L)).thenReturn(userEntity);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(30));

        verify(usersServiceImpl, times(1)).findOne(1L);
    }

    /**
     * Проверяет обработку ошибки при попытке получить пользователя по-несуществующему ID.
     * Ожидает, что запрос возвращает статус 404 Not Found и сообщение об ошибке.
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    @Test
    @DisplayName("Обработка ошибки при получении несуществующего пользователя")
    void testGetUserByIdWhenNotFound() throws Exception {
        when(usersServiceImpl.findOne(1L)).thenThrow(new UserNotFoundException("Пользователь с таким id не найден!"));

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь с таким id не найден!"));

        verify(usersServiceImpl, times(1)).findOne(1L);
    }

    /**
     * Проверяет эндпоинт POST /users для создания нового пользователя.
     * Ожидает, что запрос возвращает статус 200 OK.
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    @Test
    @DisplayName("Создание нового пользователя")
    void testCreateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");
        userDto.setAge(30);

        doNothing().when(usersServiceImpl).save(any(UserEntity.class));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(usersServiceImpl, times(1)).save(any(UserEntity.class));
    }

    /**
     * Проверяет обработку ошибок валидации при создании пользователя с некорректными данными.
     * Ожидает, что запрос возвращает статус 400 Bad Request и сообщения об ошибках валидации.
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    @Test
    @DisplayName("Обработка ошибок валидации при создании пользователя")
    void testCreateUserWithInvalidData() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("John123"); // содержит цифры
        userDto.setEmail("invalid-email"); // некорректный email
        userDto.setAge(-1); // некорректный возраст

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Имя не должно содержать цифры")))
                .andExpect(jsonPath("$.message").value(containsString("Email не должен содержать запрещенные символы")))
                .andExpect(jsonPath("$.message").value(containsString("Возраст должен быть больше 0 лет")));

        verify(usersServiceImpl, never()).save(any(UserEntity.class));
    }

    /**
     * П kids эндпоинт PUT /users/{id} для обновления пользователя.
     * Ожидает, что запрос возвращает статус 200 OK.
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    @Test
    @DisplayName("Обновление данных пользователя")
    void testUpdateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Jane Doe");
        userDto.setEmail("jane@example.com");
        userDto.setAge(25);

        UserEntity updatedUserEntity = new UserEntity("Jane Doe", "jane@example.com", 25);
        updatedUserEntity.setId(1L);
        when(usersServiceImpl.update(eq(1L), any(UserEntity.class))).thenReturn(updatedUserEntity);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.age").value(25));

        verify(usersServiceImpl, times(1)).update(eq(1L), any(UserEntity.class));
    }

    /**
     * Проверяет эндпоинт DELETE /users/{id} для удаления пользователя.
     * Ожидает, что запрос возвращает статус 200 OK.
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    @Test
    @DisplayName("Удаление пользователя по идентификатору")
    void testDeleteUser() throws Exception {
        doNothing().when(usersServiceImpl).delete(1L);

        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(usersServiceImpl, times(1)).delete(1L);
    }

    /**
     * Проверяет обработку ошибки при попытке удаления несуществующего пользователя.
     * Ожидает, что запрос возвращает статус 404 Not Found и сообщение об ошибке.
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    @Test
    @DisplayName("Обработка ошибки при удалении несуществующего пользователя")
    void testDeleteWhenUserNotFound() throws Exception {
        doThrow(new UserNotFoundException("Пользователь с таким id не найден!")).when(usersServiceImpl).delete(1L);

        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь с таким id не найден!"));

        verify(usersServiceImpl, times(1)).delete(1L);
    }

    /**
     * Проверяет обработку ошибки при попытке создать пользователя с уже занятым email.
     * Ожидает, что запрос возвращает статус 400 Bad Request и сообщение об ошибке.
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    @Test
    @DisplayName("Обработка ошибки при создании пользователя с занятым email")
    void testCreateUserUserWithDuplicateEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");
        userDto.setAge(30);

        doThrow(new IllegalArgumentException("Email уже занят")).when(usersServiceImpl).save(any(UserEntity.class));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email уже занят"));

        verify(usersServiceImpl, times(1)).save(any(UserEntity.class));
    }

    /**
     * Проверяет обработку ошибок валидации при обновлении пользователя с некорректными данными.
     * Ожидает, что запрос возвращает статус 400 Bad Request и сообщения об ошибках валидации.
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    @Test
    @DisplayName("Обработка ошибок валидации при обновлении пользователя")
    void testUpdateUserWithInvalidData() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Jane123"); // содержит цифры
        userDto.setEmail("invalid-email"); // некорректный email
        userDto.setAge(-1); // некорректный возраст

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Имя не должно содержать цифры")))
                .andExpect(jsonPath("$.message").value(containsString("Email не должен содержать запрещенные символы")))
                .andExpect(jsonPath("$.message").value(containsString("Возраст должен быть больше 0 лет")));

        verify(usersServiceImpl, never()).update(anyLong(), any(UserEntity.class));
    }

    /**
     * Проверяет обработку ошибки при попытке обновления несуществующего пользователя.
     * Ожидает, что запрос возвращает статус 404 Not Found и сообщение об ошибке.
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    @Test
    @DisplayName("Обработка ошибки при обновлении несуществующего пользователя")
    void testUpdateWhenUserNotFound() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Jane Doe");
        userDto.setEmail("jane@example.com");
        userDto.setAge(25);

        doThrow(new UserNotFoundException("Пользователь с таким id не найден!"))
                .when(usersServiceImpl).update(eq(1L), any(UserEntity.class));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Пользователь с таким id не найден!"));

        // Проверка вызова сервиса
        verify(usersServiceImpl, times(1)).update(eq(1L), any(UserEntity.class));
    }

    /**
     * Проверяет эндпоинт GET /users при возврате пустого списка пользователей.
     * Ожидает, что запрос возвращает статус 200 OK и пустой массив.
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    @Test
    @DisplayName("Получение пустого списка пользователей")
    void testGetAllUsersWhenEmptyList() throws Exception {
        when(usersServiceImpl.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(usersServiceImpl, times(1)).findAll();
    }

    /**
     * Проверяет создание пользователя с граничными значениями полей.
     * Ожидает, что запрос возвращает статус 200 OK.
     *
     * @throws Exception если произошла ошибка при выполнении запроса
     */
    @Test
    @DisplayName("Создание пользователя с граничными значениями")
    void testCreateUserUserBoundaryValues() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Ab"); // минимальная длина имени (2 символа)
        userDto.setEmail("test@example.com");
        userDto.setAge(150); // максимальный возраст

        doNothing().when(usersServiceImpl).save(any(UserEntity.class));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(usersServiceImpl, times(1)).save(any(UserEntity.class));
    }
}