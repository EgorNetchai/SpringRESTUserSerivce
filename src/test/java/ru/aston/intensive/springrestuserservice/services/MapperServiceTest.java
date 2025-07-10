package ru.aston.intensive.springrestuserservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.TestPropertySource;
import ru.aston.intensive.springrestuserservice.dto.UserDto;
import ru.aston.intensive.springrestuserservice.models.UserEntity;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тестовый класс для проверки сервиса {@link MapperService}.
 * Проверяет корректность преобразования между {@link UserEntity} и {@link UserDto}
 * с использованием параметризованных тестов.
 */
@TestPropertySource(locations = "/application-test.yaml")
public class MapperServiceTest {

    private MapperService mapperService;

    @BeforeEach
    void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        mapperService = new MapperService(modelMapper);
    }

    /**
     * Предоставляет тестовые данные для проверки преобразования {@link UserEntity} в {@link UserDto}.
     *
     * @return Поток объектов {@link UserEntity} для тестирования
     */
    private static Stream<UserEntity> userEntityProvider() {
        return Stream.of(
                new UserEntity("John Doe", "john@example.com", 30),
                new UserEntity("Иван Иванов", "ivan@example.com", 25),
                new UserEntity("", "empty@example.com", 0),
                new UserEntity(null, null, 0),
                new UserEntity("Anna Smith", "anna@example.com", 100)
        );
    }

    /**
     * Предоставляет тестовые данные для проверки преобразования {@link UserDto} в {@link UserEntity}.
     *
     * @return Поток объектов {@link UserDto} для тестирования
     */
    private static Stream<UserDto> userDtoProvider() {
        UserDto dto1 = new UserDto();
        dto1.setName("John Doe");
        dto1.setEmail("john@example.com");
        dto1.setAge(30);

        UserDto dto2 = new UserDto();
        dto2.setName("Иван Иванов");
        dto2.setEmail("ivan@example.com");
        dto2.setAge(25);

        UserDto dto3 = new UserDto();
        dto3.setName("");
        dto3.setEmail("empty@example.com");
        dto3.setAge(0);

        UserDto dto4 = new UserDto();
        dto4.setName(null);
        dto4.setEmail(null);
        dto4.setAge(0);

        UserDto dto5 = new UserDto();
        dto5.setName("Anna Smith");
        dto5.setEmail("anna@example.com");
        dto5.setAge(100);

        return Stream.of(dto1, dto2, dto3, dto4, dto5);
    }

    /**
     * Проверяет преобразование объекта {@link UserEntity} в {@link UserDto}.
     * Убеждается, что поля name, email и age корректно мапятся.
     *
     * @param userEntity Объект {@link UserEntity} для преобразования
     */
    @ParameterizedTest
    @MethodSource("userEntityProvider")
    @DisplayName("Проверка преобразования UserEntity в UserDto")
    void testConvertToUserDto(UserEntity userEntity) {
        UserDto userDto = mapperService.convertToUserDto(userEntity);

        assertEquals(userEntity.getName(), userDto.getName(), "Имя должно совпадать");
        assertEquals(userEntity.getEmail(), userDto.getEmail(), "Email должен совпадать");
        assertEquals(userEntity.getAge(), userDto.getAge(), "Возраст должен совпадать");
    }


    /**
     * Проверяет преобразование объекта {@link UserDto} в {@link UserEntity}.
     * Убеждается, что поля name, email и age корректно мапятся.
     *
     * @param userDto Объект {@link UserDto} для преобразования
     */
    @ParameterizedTest
    @MethodSource("userDtoProvider")
    @DisplayName("Проверка преобразования UserDto в UserEntity")
    void testConvertToUserEntity(UserDto userDto) {
        UserEntity userEntity = mapperService.convertToUserEntity(userDto);

        assertEquals(userDto.getName(), userEntity.getName(), "Имя должно совпадать");
        assertEquals(userDto.getEmail(), userEntity.getEmail(), "Email должен совпадать");
        assertEquals(userDto.getAge(), userEntity.getAge(), "Возраст должен совпадать");
    }
}