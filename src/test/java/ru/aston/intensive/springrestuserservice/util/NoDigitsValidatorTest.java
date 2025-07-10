package ru.aston.intensive.springrestuserservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тестовый класс для проверки валидатора {@link NoDigitsValidator}.
 * Проверяет корректность валидации строк, не содержащих цифры, с использованием параметризованных тестов.
 */
@TestPropertySource(locations = "/application-test.yaml")
public class NoDigitsValidatorTest {


    private NoDigitsValidator validator;

    @BeforeEach
    void setup() {
        validator = new NoDigitsValidator();
    }

    /**
     * Проверяет, что валидные имена, не содержащие цифры, проходят валидацию.
     * Включает строки с буквами, пробелами, дефисами, специальными символами и случай null.
     *
     * @param name Имя для проверки
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "John Doe",
            "Иван Иванов",
            "John-Doe",
            "Анна-Мария",
            "Marie Curie"
    })
    @NullSource
    @DisplayName("Проверка валидных имен без цифр")
    void testValidName(String name) {
        assertTrue(validator.isValid(name, null), "Имя " + name + " должно быть валидным");
    }

    /**
     * Проверяет, что имена, содержащие цифры, не проходят валидацию.
     * Включает случаи с цифрами в начале, середине, конце и в сочетании с другими символами.
     *
     * @param name Имя для проверки
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "John123",
            "Иван2",
            "123John",
            "John_123_Doe",
            "Анна2023",
            "1",
            "John-2-Doe",
            "12_34",
            "Marie_Curie",
            "Hello.World",
            "@#$%^&",
            ""
    })
    @DisplayName("Проверка невалидных имен с цифрами")
    void testInvalidNameWithDigits(String name) {
        assertFalse(validator.isValid(name, null), "Имя " + name + " должно быть невалидным");
    }
}