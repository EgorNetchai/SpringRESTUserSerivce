package ru.aston.intensive.springrestuserservice.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.intensive.springrestuserservice.dto.UserDto;
import ru.aston.intensive.springrestuserservice.models.User;
import ru.aston.intensive.springrestuserservice.services.UsersService;
import ru.aston.intensive.springrestuserservice.util.UserNotCreatedException;
import ru.aston.intensive.springrestuserservice.util.UserErrorResponse;
import ru.aston.intensive.springrestuserservice.util.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST-контроллер для управления пользователями.
 * Обрабатывает HTTP-запросы для выполнения CRUD-операций над пользователями.
 */
@RestController
@RequestMapping("/users")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    private final UsersService usersService;
    private final ModelMapper modelMapper;

    /**
     * Конструктор контроллера пользователей.
     *
     * @param usersService сервис для работы с пользователями
     * @param modelMapper маппер для преобразования объектов между User и UserDto
     */
    @Autowired
    public UsersController(UsersService usersService, ModelMapper modelMapper) {
        this.usersService = usersService;
        this.modelMapper = modelMapper;
    }

    /**
     * Получает список всех пользователей.
     *
     * @return список пользователей в формате UserDto
     *
     * @throws UserNotFoundException если пользователи не найдены
     */
    @GetMapping
    public List<UserDto> getUsers() {
        logger.info("Получен GET-запрос для получения списка всех пользователей");

        List<UserDto> users = usersService.findAll().stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());

        logger.info("Возвращено {} пользователей", users.size(
        ));

        return users;
    }

    /**
     * Получает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     *
     * @return пользователь в формате UserDto
     *
     * @throws UserNotFoundException если пользователь не найден
     */
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") Long id) {
        logger.info("Получен GET-запрос для получения пользователя с ID: {}", id);

        UserDto userDto = convertToUserDto(usersService.findOne(id));
        logger.info("Пользователь с ID {} успешно возвращен", id);

        return userDto;
    }

    /**
     * Создает нового пользователя.
     *
     * @param userDto       данные пользователя в формате UserDto
     * @param bindingResult результат валидации
     *
     * @return HTTP-статус OK при успешном создании
     *
     * @throws UserNotCreatedException если данные пользователя некорректны
     * @throws IllegalArgumentException если email уже занят
     */
    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid UserDto userDto,
                                             BindingResult bindingResult) {

        logger.info("Получен POST-запрос для создания пользователя с email: {}", userDto.getEmail());

        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();

            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }

            logger.error("Ошибки валидации при создании пользователя: {}", errorMsg);
            throw new UserNotCreatedException(errorMsg.toString());
        }

        usersService.save(convertToUser(userDto));
        logger.info("Пользователь с email {} успешно создан", userDto.getEmail());

        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param id            идентификатор пользователя
     * @param userDto       обновленные данные пользователя в формате UserDto
     * @param bindingResult результат валидации
     *
     * @return HTTP-статус OK при успешном обновлении
     *
     * @throws UserNotCreatedException  если данные пользователя некорректны
     * @throws UserNotFoundException    если пользователь не найден
     * @throws IllegalArgumentException если email уже занят
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id,
                                                 @RequestBody @Valid UserDto userDto,
                                                 BindingResult bindingResult) {
        logger.info("Получен PUT-запрос для обновления пользователя с ID: {}", id);

        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();

            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }

            logger.error("Ошибки валидации при обновлении пользователя с ID {}: {}", id, errorMsg);
            throw new UserNotCreatedException(errorMsg.toString());
        }

        User updatedUser = usersService.update(id, convertToUser(userDto));
        logger.info("Пользователь с ID {} успешно обновлен", id);

        return ResponseEntity.ok(convertToUserDto(updatedUser));
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     *
     * @return HTTP-статус OK при успешном удалении
     *
     * @throws UserNotFoundException если пользователь не найден
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") Long id) {
        logger.info("Получен DELETE-запрос для удаления пользователя с ID: {}", id);
        usersService.delete(id);

        logger.info("Пользователь с ID {} успешно удален", id);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     * Обрабатывает исключение UserNotFoundException.
     *
     * @param e исключение, указывающее на отсутствие пользователя
     *
     * @return ответ с сообщением об ошибке и статусом NOT_FOUND
     */
    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotFoundException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage() != null ? e.getMessage() : "Пользователь с таким id не найден!",
                LocalDateTime.now()
        );

        logger.error("Ошибка: {}", response.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Обрабатывает исключение UserNotCreatedException.
     *
     * @param e исключение, указывающее на ошибку при создании пользователя
     *
     * @return ответ с сообщением об ошибке и статус Nelson status code: BAD_REQUEST
     */
    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserNotCreatedException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(),
                LocalDateTime.now()
        );

        logger.error("Ошибка создания/обновления пользователя: {}", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает исключение IllegalArgumentException.
     *
     * @param e исключение, указывающее на некорректные аргументы (например, занятый email)
     *
     * @return ответ с сообщением об ошибке и статусом BAD_REQUEST
     */
    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(IllegalArgumentException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(),
                LocalDateTime.now()
        );

        logger.error("Ошибка: {}", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Преобразует объект UserDto в объект User.
     *
     * @param userDto объект UserDto
     *
     * @return объект User
     */
    private User convertToUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    /**
     * Преобразует объект User в объект UserDto.
     *
     * @param user объект User
     *
     * @return объект UserDto
     */
    private UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

}
