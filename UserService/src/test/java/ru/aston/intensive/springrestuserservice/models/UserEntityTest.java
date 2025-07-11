package ru.aston.intensive.springrestuserservice.models;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестовый класс для проверки функциональности и валидации {@link UserEntity}.
 */
@TestPropertySource(locations = "/application-test.yaml")
public class UserEntityTest {

    private static Validator validator;

    /**
     * Инициализирует валидатор перед выполнением всех тестов.
     */
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Проверяет корректность установки и получения значений полей {@link UserEntity}.
     */
    @Test
    @DisplayName("Проверка корректной установки и получения значений полей")
    void shouldSetAndGetFieldsCorrectly() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        user.setId(1L);
        user.setCreated_at(LocalDateTime.now());
        user.setUpdated_at(LocalDateTime.now());

        assertEquals(1L, user.getId(), "ID должен быть установлен корректно");
        assertEquals("John Doe", user.getName(), "Имя должно быть установлено корректно");
        assertEquals("john@example.com", user.getEmail(), "Email должен быть установлен корректно");
        assertEquals(30, user.getAge(), "Возраст должен быть установлен корректно");
        assertNotNull(user.getCreated_at(), "Время создания должно быть установлено");
        assertNotNull(user.getUpdated_at(), "Время обновления должно быть установлено");
    }

    /**
     * Проверяет, что валидный объект {@link UserEntity} проходит валидацию без ошибок.
     */
    @Test
    @DisplayName("Проверка валидации для корректного пользователя")
    void shouldPassValidationForValidUser() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "Корректный пользователь не должен иметь ошибок валидации");
    }

    /**
     * Проверяет, что пустое имя вызывает ошибки валидации.
     */
    @Test
    @DisplayName("Проверка валидации для пустого имени")
    void shouldFailValidationForEmptyName() {
        UserEntity user = new UserEntity("", "john@example.com", 30);

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        Set<String> violationMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertEquals(3, violations.size(), "Пустое имя должно вызывать две ошибки валидации");
        assertTrue(violationMessages.contains("Имя не должно быть пустым"), "Должна быть ошибка о пустом имени");
        assertTrue(violationMessages.contains("Имя должно быть от 2 до 50 символов"), "Должна быть ошибка о длине имени");
    }

    /**
     * Проверяет, что имя, состоящее из одного пробела, вызывает ошибки валидации.
     */
    @Test
    @DisplayName("Проверка валидации для имени из одного пробела")
    void shouldFailValidationForSingleSpaceName() {
        UserEntity user = new UserEntity(" ", "john@example.com", 30);

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        Set<String> violationMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertEquals(2, violations.size(), "Имя из одного пробела должно вызывать две ошибки валидации");
        assertTrue(violationMessages.contains("Имя не должно быть пустым"), "Должна быть ошибка о пустом имени");
        assertTrue(violationMessages.contains("Имя должно быть от 2 до 50 символов"), "Должна быть ошибка о длине имени");
    }

    /**
     * Проверяет, что имя, состоящее из двух пробелов, вызывает ошибки валидации.
     */
    @Test
    @DisplayName("Проверка валидации для имени из двух пробелов")
    void shouldFailValidationForDoubleSpaceName() {
        UserEntity user = new UserEntity("  ", "john@example.com", 30);

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        Set<String> violationMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertEquals(1, violations.size(), "Имя из двух пробелов должно вызывать две ошибки валидации");
        assertTrue(violationMessages.contains("Имя не должно быть пустым"), "Должна быть ошибка о пустом имени");
    }

    /**
     * Проверяет, что имя, состоящее из трех пробелов, вызывает ошибки валидации.
     */
    @Test
    @DisplayName("Проверка валидации для имени из трех пробелов")
    void shouldFailValidationForTripleSpaceName() {
        UserEntity user = new UserEntity("   ", "john@example.com", 30);

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        Set<String> violationMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertEquals(1, violations.size(), "Имя из трех пробелов должно вызывать две ошибки валидации");
        assertTrue(violationMessages.contains("Имя не должно быть пустым"), "Должна быть ошибка о пустом имени");
    }

    /**
     * Проверяет, что имя с цифрами вызывает ошибку валидации.
     */
    @Test
    @DisplayName("Проверка валидации для имени с цифрами")
    void shouldFailValidationForNameWithDigits() {
        UserEntity user = new UserEntity("John123", "john@example.com", 30);

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Имя с цифрами должно вызывать одну ошибку валидации");
        assertEquals("Имя не должно содержать цифры", violations.iterator().next().getMessage(),
                "Должна быть ошибка о наличии цифр в имени");
    }

    /**
     * Проверяет, что null в поле имени вызывает ошибки валидации.
     */
    @Test
    @DisplayName("Проверка валидации для null в имени")
    void shouldFailValidationForNullName() {
        UserEntity user = new UserEntity(null, "john@example.com", 30);

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        Set<String> violationMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertEquals(1, violations.size(), "Null в имени должно вызывать две ошибки валидации");
        assertTrue(violationMessages.contains("Имя не должно быть пустым"), "Должна быть ошибка о пустом имени");
    }

    /**
     * Проверяет, что слишком короткое имя вызывает ошибку валидации.
     */
    @Test
    @DisplayName("Проверка валидации для слишком короткого имени")
    void shouldFailValidationForShortName() {
        UserEntity user = new UserEntity("A", "john@example.com", 30);

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Слишком короткое имя должно вызывать одну ошибку валидации");
        assertEquals("Имя должно быть от 2 до 50 символов", violations.iterator().next().getMessage(),
                "Должна быть ошибка о длине имени");
    }

    /**
     * Проверяет, что слишком длинное имя вызывает ошибку валидации.
     */
    @Test
    @DisplayName("Проверка валидации для слишком длинного имени")
    void shouldFailValidationForLongName() {
        String longName = "A".repeat(51); // 51 символ
        UserEntity user = new UserEntity(longName, "john@example.com", 30);

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Слишком длинное имя должно вызывать одну ошибку валидации");
        assertEquals("Имя должно быть от 2 до 50 символов", violations.iterator().next().getMessage(),
                "Должна быть ошибка о длине имени");
    }

    /**
     * Проверяет, что некорректный email вызывает ошибки валидации.
     */
    @Test
    @DisplayName("Проверка валидации для некорректного email")
    void shouldFailValidationForInvalidEmail() {
        UserEntity user = new UserEntity("John Doe", "invalid-email", 30);

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        Set<String> violationMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertEquals(2, violations.size(), "Некорректный email должен вызывать две ошибки валидации");
        assertTrue(violationMessages.contains(""), "Должна быть стандартная ошибка валидации email");
        assertTrue(violationMessages.contains("Email не должен содержать запрещенные символы и должен содержать точку в домене"),
                "Должна быть пользовательская ошибка валидации email");
    }

    /**
     * Проверяет, что отрицательный возраст вызывает ошибку валидации.
     */
    @Test
    @DisplayName("Проверка валидации для отрицательного возраста")
    void shouldFailValidationForInvalidAge() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", -1);

        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Отрицательный возраст должен вызывать одну ошибку валидации");
        assertEquals("Возраст должен быть больше 0 лет", violations.iterator().next().getMessage(),
                "Должна быть ошибка о недопустимом возрасте");
    }
}