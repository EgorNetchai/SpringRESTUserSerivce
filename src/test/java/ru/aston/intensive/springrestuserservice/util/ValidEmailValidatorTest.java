package ru.aston.intensive.springrestuserservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестовый класс для проверки валидатора {@link ValidEmailValidator}.
 * Проверяет корректность валидации email-адресов согласно заданным правилам.
 */
@TestPropertySource(locations = "/application-test.yaml")
public class ValidEmailValidatorTest {

    private ValidEmailValidator validator;

    @BeforeEach
    void setup() {
        validator = new ValidEmailValidator();
    }

    /**
     * Проверяет, что валидные email-адреса проходят валидацию.
     * Включает различные форматы email и случай null.
     *
     * @param email Email-адрес для проверки
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "john@example.com",
            "john.doe@sub.example.com",
            "test+label@domain.co.uk",
            "user123@company.org",
            "name.surname@domain.io"
    })
    @DisplayName("Проверка валидных email-адресов")
    void testValidEmail(String email) {
       assertTrue(validator.isValid(email, null), "Email " + email + " должен быть валидным");
    }

    /**
     * Проверяет, что невалидные email-адреса не проходят валидацию.
     * Включает случаи с отсутствием символа @, отсутствием домена, пробелами,
     * запрещенными символами, некорректными доменами и пустой строкой.
     *
     * @param email Email-адрес для проверки
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "john@.com",
            "john@example",
            "john @example.com",
            "john#doe@example.com",
            "@example.com",
            "john@domain..com",
            "john@-domain.com",
            "",
            "john@domain",
            "john@.example.com",
            "john@example..com",
    })
    @DisplayName("Проверка невалидных email-адресов")
    void testInvalidEmail(String email) {
        assertFalse(validator.isValid(email, null), "Email  " + email + " должен быть невалидным");
    }
}