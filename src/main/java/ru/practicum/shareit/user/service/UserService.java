package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {
    UserDto createUser(User user);

    UserDto getUserById(long userId);

    UserDto updateUser(long userId, User updatedUser);

    void deleteUser(long userId);

    List<UserDto> getAllUsers();
}
