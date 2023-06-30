package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.RepositoryUser;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final RepositoryUser repository;


    @Override
    public User createUser(User user) {
        return repository.save(user);
    }

    @Override
    public User getUserById(long userId) {
        return repository.findById(userId).orElse(null);
    }

    @Override
    public User updateUser(long userId, User updatedUser) {
        User user = repository.findById(userId).orElse(null);
        if (user == null) {
            throw new DataNotFoundException("User not found");
        }
        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }

        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }

        return repository.save(user);
    }

    @Override
    public void deleteUser(long userId) {
        if (!repository.existsById(userId)) {
            throw new DataNotFoundException("User not found");
        }
        repository.deleteById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

}
