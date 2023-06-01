package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.storage.StorageUser;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final StorageUser storageUser;

    public UserServiceImpl(StorageUser storageUser) {
        this.storageUser = storageUser;
    }

    @Override
    public UserDto createUser(User user) {
        return storageUser.createUser(user);
    }

    @Override
    public UserDto getUserById(long userId) {
        return storageUser.getUserById(userId);
    }

    @Override
    public UserDto updateUser(long userId, User updatedUser) {
        return storageUser.updateUser(userId, updatedUser);
    }

    @Override
    public void deleteUser(long userId) {
        storageUser.deleteUser(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return storageUser.getAllUsers();
    }
}
