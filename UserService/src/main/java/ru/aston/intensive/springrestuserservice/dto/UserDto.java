package ru.aston.intensive.springrestuserservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import ru.aston.intensive.springrestuserservice.util.NoDigits;
import ru.aston.intensive.springrestuserservice.util.ValidEmail;

/**
 * Класс DTO (Data Transfer Object) для передачи данных о пользователе.
 * Используется для валидации и передачи данных между слоями приложения
 */
public class UserDto {

    /**
     * Имя пользователя. Должно быть не пустым и содержать от 2 до 50 символов.
     * Не должно содержать цифры.
     */
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @NoDigits
    private String name;

    /**
     * Электронная почта пользователя. Должна быть уникальной и корректного формата.
     */
    @NotEmpty(message = "Email не должен быть пустым")
    @Email(message = "")
    @ValidEmail
    private String email;

    /**
     * Возраст пользователя. Должен быть больше 0 и меньше 150 лет.
     */
    @Min(value = 0, message = "Возраст должен быть больше 0 лет")
    @Max(value = 150, message = "Возраст должен быть меньше 150 лет")
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
