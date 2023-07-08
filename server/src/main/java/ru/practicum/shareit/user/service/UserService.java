package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User getUserById(long userId);

    User updateUser(long userId, User updatedUser);

    void deleteUser(long userId);

    List<User> getAllUsers();
}
