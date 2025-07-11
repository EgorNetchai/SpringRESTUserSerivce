package ru.aston.intensive.springrestuserservice.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.intensive.springrestuserservice.dto.UserDto;
import ru.aston.intensive.springrestuserservice.models.UserEntity;
import ru.aston.intensive.springrestuserservice.services.MapperService;
import ru.aston.intensive.springrestuserservice.services.UsersServiceCrud;
import ru.aston.intensive.springrestuserservice.util.UserNotCreatedException;
import ru.aston.intensive.springrestuserservice.util.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST-контроллер для управления пользователями.
 * Обрабатывает HTTP-запросы для выполнения CRUD-операций над пользователями.
 */
@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersServiceCrud usersServiceCRUD;
    private final MapperService mapperService;

    /**
     * Конструктор контроллера пользователей.
     *
     * @param usersServiceCRUD сервис для работы с пользователями
     * @param mapperService маппер для преобразования объектов между UserEntity и UserDto
     */
    @Autowired
    public UsersController(UsersServiceCrud usersServiceCRUD, MapperService mapperService) {
        this.usersServiceCRUD = usersServiceCRUD;
        this.mapperService = mapperService;
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
        return usersServiceCRUD.findAll().stream()
                .map(mapperService::convertToUserDto)
                .collect(Collectors.toList());
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
        return mapperService.convertToUserDto(usersServiceCRUD.findOne(id));
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
    public ResponseEntity<HttpStatus> createUser(@RequestBody @Valid UserDto userDto,
                                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();

            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }

            throw new UserNotCreatedException(errorMsg.toString());
        }

        usersServiceCRUD.save(mapperService.convertToUserEntity(userDto));

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

        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();

            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }

            throw new UserNotCreatedException(errorMsg.toString());
        }

        UserEntity updatedUserEntity = usersServiceCRUD.update(id, mapperService.convertToUserEntity(userDto));

        return ResponseEntity.ok(mapperService.convertToUserDto(updatedUserEntity));
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
        usersServiceCRUD.delete(id);

        return ResponseEntity.ok(HttpStatus.OK);
    }
}
