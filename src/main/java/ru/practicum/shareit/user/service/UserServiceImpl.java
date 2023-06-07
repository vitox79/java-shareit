package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.repository.RepositoryUser;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final RepositoryUser storageUser;

    public UserServiceImpl(RepositoryUser storageUser) {
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
