package ru.aston.intensive.springrestuserservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import ru.aston.intensive.springrestuserservice.util.NoDigits;
import ru.aston.intensive.springrestuserservice.util.ValidEmail;

import java.time.LocalDateTime;

/**
 * Класс, представляющий сущность пользователя в системе.
 * Сопоставляется с таблицей "users" в базе данных.
 */
@Entity
@Table(name = "users")
public class UserEntity {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя. Должно быть не пустым и содержать от 2 до 50 символов.
     * Не должно содержать цифры.
     */
    @Column(name = "name")
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @NoDigits
    private String name;

    /**
     * Электронная почта пользователя. Должна быть уникальной и корректного формата.
     */
    @Column(name = "email")
    @NotEmpty(message = "Email не должен быть пустым")
    @Email(message = "")
    @ValidEmail
    private String email;

    /**
     * Возраст пользователя. Должен быть больше 0 и меньше 150 лет.
     */
    @Column(name = "age")
    @Min(value = 0, message = "Возраст должен быть больше 0 лет")
    @Max(value = 150, message = "Возраст должен быть меньше 150 лет")
    private int age;

    /**
     * Время создания записи о пользователе.
     */
    @Column(name = "created_at")
    private LocalDateTime created_at;

    /**
     * Время последнего обновления записи о пользователе.
     */
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    /**
     * Конструктор по умолчанию.
     */
    public UserEntity() {}

    /**
     * Конструктор для создания нового пользователя.
     *
     * @param name  имя пользователя
     * @param email адрес электронной почты
     * @param age   возраст пользователя
     */
    public UserEntity(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}
