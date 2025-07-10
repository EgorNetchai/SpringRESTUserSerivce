package ru.aston.intensive.springrestuserservice.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.aston.intensive.common.dto.UserNotificationDto;
import ru.aston.intensive.springrestuserservice.dto.UserDto;
import ru.aston.intensive.springrestuserservice.models.UserEntity;

/**
 * Сервис для преобразования сущностей в DTO и обратно.
 */
@Service
public class MapperService {

    /** Экземпляр ModelMapper для маппинга объектов. */
    private final ModelMapper modelMapper;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param modelMapper Экземпляр ModelMapper
     */
    public MapperService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Преобразует сущность пользователя в DTO для REST API.
     *
     * @param userEntity Сущность пользователя
     *
     * @return DTO пользователя
     */
    public UserDto convertToUserDto(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDto.class);
    }

    /**
     * Преобразует DTO пользователя в сущность.
     *
     * @param userDto DTO пользователя
     *
     * @return Сущность пользователя
     */
    public UserEntity convertToUserEntity(UserDto userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }

    /**
     * Преобразует сущность пользователя в DTO для уведомлений Kafka.
     *
     * @param userEntity Сущность пользователя
     *
     * @return DTO для уведомлений
     */
    public UserNotificationDto convertToUserNotificationDto(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserNotificationDto.class);
    }
}