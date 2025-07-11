package ru.aston.intensive.springrestuserservice.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import ru.aston.intensive.springrestuserservice.models.UserEntity;
import ru.aston.intensive.springrestuserservice.repositories.UsersRepository;
import ru.aston.intensive.springrestuserservice.util.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестовый класс для проверки функциональности сервиса {@link UsersServiceCrudImpl}.
 */
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "/application-test.yaml")
public class UsersServiceCrudImplTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersServiceCrudImpl usersServiceImpl;

    /**
     * Тестирует метод получения списка всех пользователей.
     * Проверяет, что возвращается корректный список и вызывается соответствующий метод репозитория.
     */
    @Test
    @DisplayName("Получение списка всех пользователей")
    void testFindAll() {
        UserEntity userEntity = new UserEntity("John Doe", "john@example.com", 30);
        when(usersRepository.findAll()).thenReturn(List.of(userEntity));

        List<UserEntity> userEntities = usersServiceImpl.findAll();

        assertEquals(1, userEntities.size());
        assertEquals("John Doe", userEntities.get(0).getName());

        verify(usersRepository, times(1)).findAll();
    }

    /**
     * Тестирует выброс исключения, когда список пользователей пуст.
     */
    @Test
    @DisplayName("Обработка пустого списка пользователей")
    void testFindAllWhenEmptyList() {
        when(usersRepository.findAll()).thenReturn(List.of());

        assertThrows(UserNotFoundException.class, () -> usersServiceImpl.findAll());
        verify(usersRepository, times(1)).findAll();
    }

    /**
     * Тестирует метод поиска пользователя по идентификатору, когда пользователь не найден.
     * Проверяет, что выбрасывается исключение {@link UserNotFoundException}.
     */
    @Test
    @DisplayName("Поиск несуществующего пользователя по идентификатору")
    void testFindOneWhenNotFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> usersServiceImpl.findOne(1L));
        verify(usersRepository, times(1)).findById(1L);
    }

    /**
     * Тестирует метод поиска пользователя по идентификатору, когда пользователь существует.
     * Проверяет, что возвращается корректный пользователь.
     */
    @Test
    @DisplayName("Успешный поиск пользователя по идентификатору")
    void testFindOneSuccess() {
        UserEntity userEntity = new UserEntity("John Doe", "john@example.com", 30);
        userEntity.setId(1L);

        when(usersRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        UserEntity foundUserEntity = usersServiceImpl.findOne(1L);

        assertEquals("John Doe", foundUserEntity.getName());
        assertEquals("john@example.com", foundUserEntity.getEmail());

        verify(usersRepository, times(1)).findById(1L);
    }

    /**
     * Тестирует метод сохранения нового пользователя.
     * Проверяет, что пользователь сохраняется с установленными временными метками.
     */
    @Test
    @DisplayName("Успешное сохранение нового пользователя")
    void testSaveSuccess() {
        UserEntity userEntity = new UserEntity("Jane Doe", "jane@example.com", 25);

        when(usersRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(usersRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        usersServiceImpl.save(userEntity);

        verify(usersRepository, times(1)).existsByEmail("jane@example.com");
        verify(usersRepository, times(1)).save(userEntity);

        assertNotNull(userEntity.getCreated_at());
        assertNotNull(userEntity.getUpdated_at());
    }

    /**
     * Тестирует выброс исключения при попытке сохранить пользователя с занятым email.
     */
    @Test
    @DisplayName("Обработка ошибки при сохранении пользователя с занятым email")
    void testSaveWhenEmailAlreadyExists() {
        UserEntity userEntity = new UserEntity("Jane Doe", "jane@example.com", 25);

        when(usersRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> usersServiceImpl.save(userEntity));

        verify(usersRepository, times(1)).existsByEmail("jane@example.com");
        verify(usersRepository, never()).save(any(UserEntity.class));
    }

    /**
     * Тестирует метод обновления пользователя с изменением данных.
     * Проверяет, что пользователь обновляется корректно, сохраняя исходную дату создания.
     */
    @Test
    @DisplayName("Успешное обновление данных пользователя")
    void testUpdateSuccess() {
        UserEntity existingUserEntity = new UserEntity("John Doe", "john@example.com", 30);
        existingUserEntity.setId(1L);
        existingUserEntity.setCreated_at(LocalDateTime.now().minusDays(1));

        UserEntity updatedUserEntity = new UserEntity("John Smith", "john.smith@example.com", 31);
        UserEntity savedUserEntity = new UserEntity("John Smith", "john.smith@example.com", 31);
        savedUserEntity.setId(1L);
        savedUserEntity.setCreated_at(existingUserEntity.getCreated_at());
        savedUserEntity.setUpdated_at(LocalDateTime.now());

        when(usersRepository.findById(1L)).thenReturn(Optional.of(existingUserEntity));
        when(usersRepository.existsByEmail("john.smith@example.com")).thenReturn(false);
        when(usersRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        UserEntity result = usersServiceImpl.update(1L, updatedUserEntity);

        verify(usersRepository, times(1)).findById(1L);
        verify(usersRepository, times(1)).existsByEmail("john.smith@example.com");
        verify(usersRepository, times(1)).save(existingUserEntity);

        assertEquals("John Smith", result.getName());
        assertEquals("john.smith@example.com", result.getEmail());
        assertEquals(31, result.getAge());
        assertEquals(existingUserEntity.getCreated_at(), result.getCreated_at());
        assertNotNull(result.getUpdated_at());
        assertEquals(1L, result.getId());
    }

    /**
     * Тестирует выброс исключения при попытке обновить несуществующего пользователя.
     */
    @Test
    @DisplayName("Обработка ошибки при обновлении несуществующего пользователя")
    void testUpdateWhenUserNotFound() {
        UserEntity updatedUserEntity = new UserEntity("John Smith", "john.smith@example.com", 31)
                ;
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> usersServiceImpl.update(1L, updatedUserEntity));

        verify(usersRepository, times(1)).findById(1L);
        verify(usersRepository, never()).save(any(UserEntity.class));
    }

    /**
     * Тестирует выброс исключения при попытке обновить пользователя с занятым email.
     */
    @Test
    @DisplayName("Обработка ошибки при обновлении пользователя с занятым email")
    void testUpdateWhenEmailAlreadyExists() {
        UserEntity existingUserEntity = new UserEntity("John Doe", "john@example.com", 30);
        existingUserEntity.setId(1L);
        UserEntity updatedUserEntity = new UserEntity("John Smith", "taken@example.com", 31);

        when(usersRepository.findById(1L)).thenReturn(Optional.of(existingUserEntity));
        when(usersRepository.existsByEmail("taken@example.com")).thenReturn(true);

        System.out.println("Existing email: " + existingUserEntity.getEmail());
        System.out.println("Updated email: " + updatedUserEntity.getEmail());

        assertThrows(IllegalArgumentException.class, () -> usersServiceImpl.update(1L, updatedUserEntity));

        verify(usersRepository, times(1)).findById(1L);
        verify(usersRepository, times(1)).existsByEmail("taken@example.com");
        verify(usersRepository, never()).save(any(UserEntity.class));
    }

    /**
     * Тестирует метод удаления пользователя по идентификатору.
     * Проверяет, что пользователь удаляется, если он существует.
     */
    @Test
    @DisplayName("Успешное удаление пользователя")
    void testDeleteSuccess() {
        when(usersRepository.existsById(1L)).thenReturn(true);

        usersServiceImpl.delete(1L);

        verify(usersRepository, times(1)).existsById(1L);
        verify(usersRepository, times(1)).deleteById(1L);
    }

    /**
     * Тестирует выброс исключения при попытке удалить несуществующего пользователя.
     */
    @Test
    @DisplayName("Обработка ошибки при удалении несуществующего пользователя")
    void testDeleteWhenUserNotFound() {
        when(usersRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> usersServiceImpl.delete(1L));

        verify(usersRepository, times(1)).existsById(1L);
        verify(usersRepository, never()).deleteById(1L);
    }
}