package ru.aston.intensive.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Класс DTO (Data Transfer Object) для передачи данных об уведомлении пользователя.
 * Содержит информацию об адресе электронной почты и типе события.
 */
public class UserNotificationDto {
    /** Адрес электронной почты пользователя, получающего уведомление. */
    @JsonProperty
    private String email;

    /** Тип события, связанного с уведомлением. */
    @JsonProperty
    private String eventType;

    /**
     * Возвращает адрес электронной почты пользователя.
     *
     * @return строка, содержащая адрес электронной почты
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает адрес электронной почты пользователя.
     *
     * @param email строка с адресом электронной почты
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Возвращает тип события уведомления.
     *
     * @return строка, содержащая тип события
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Устанавливает тип события уведомления.
     *
     * @param eventType строка с типом события
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}