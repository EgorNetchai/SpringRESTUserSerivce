package ru.aston.intensive.springrestuserservice.util;

/**
 * Исключение, выбрасываемое при неудачной попытке создания пользователя.
 */
public class UserNotCreatedException extends RuntimeException{

    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param msg сообщение об ошибке
     */
    public UserNotCreatedException(String msg) {
        super(msg);
    }
}
