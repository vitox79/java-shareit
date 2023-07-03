package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.RepositoryUser;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class UserServiceImplTest {
    private final RepositoryUser repository = mock(RepositoryUser.class);


    private final UserService service = new UserServiceImpl(repository);

    @Test
    void getUserWrong() {
        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            service.getUserById(-1);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getUserUnknown() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Throwable thrown = assertThrows(NotFoundException.class, () -> {
            service.getUserById(0);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void getUser() {
        User userRepository = User.builder()
                .id(1L)
                .name("name")
                .email("mail")
                .build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(userRepository));

        User user = service.getUserById(1);

        assertNotNull(user, "Null user");
        assertEquals(userRepository, user, "wrong user");
    }

    @Test
    void getUserByIdWithMapper() {
        User user = User.builder()
                .id(1L)
                .email("mail")
                .name("name").build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));

        User userOut = service.getUserById(1);

        assertNotNull(userOut, "Null user");
        assertEquals(user, userOut, "wrong user");
    }

    @Test
    void updateNameAndEmail() {
        User userRepository = User.builder()
                .id(1L)
                .name("name")
                .email("email")
                .build();
        when(repository.findById(anyLong())).thenReturn(Optional.of(userRepository));
        User userUpdate = User.builder()
                .id(1L)
                .name("name2")
                .email("mail")
                .build();
        when(repository.save(any())).thenReturn(userUpdate);
        User update = User.builder()
                .name("name2")
                .email("mail")
                .build();

        User user = service.updateUser(1, update);

        assertNotNull(user, "null user");
        assertEquals(update.getName(), user.getName(), "wrong name");
        assertEquals(update.getEmail(), user.getEmail(), "wrong mail");
    }

    @Test
    void getAllEmpty() {
        when(repository.findAll()).thenReturn(List.of());
        List<User> users = service.getAllUsers();

        assertNotNull(users, "wrong null");
        assertEquals(0, users.size(), "not empty list");
    }

    @Test
    void getAll() {
        UserDto user = UserDto.builder().id(1L).email("mail").name("name").build();
        when(repository.findAll()).thenReturn(List.of(User.builder().id(1L).email("mail").name("name").build()));
        List<User> users = service.getAllUsers();

        assertNotNull(users, "wrong null");
        assertEquals(user.getId(), users.get(0).getId(), "wrong data");
    }

    @Test
    void addNewUser() {
        User user = User.builder().name("name").email("mail").build();
        when(repository.save(any())).thenReturn(User.builder().id(1L).name("name").email("mail").build());
        UserDto userDtoNew = UserMapper.toUserDto(service.createUser(user));

        assertNotNull(userDtoNew, "null data");
        assertEquals(1, userDtoNew.getId(), "wrong id");
        assertEquals(user.getName(), userDtoNew.getName(), "wrong name");
        assertEquals(user.getEmail(), userDtoNew.getEmail(), "wrong email");
    }

    @Test
    void deleteUser() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(User.builder().build()));
        service.deleteUser(1L);
    }
}