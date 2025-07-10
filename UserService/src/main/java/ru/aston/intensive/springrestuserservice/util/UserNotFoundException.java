package ru.aston.intensive.springrestuserservice.util;

/**
 * Исключение, выбрасываемое при отсутствии пользователя в базе данных.
 */
public class UserNotFoundException extends RuntimeException{

    /**
     * Конструктор по умолчанию.
     */
    public UserNotFoundException() {
        super();
    }

    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
