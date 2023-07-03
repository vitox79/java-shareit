package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.RepositoryUser;

import java.util.List;
import java.util.Optional;

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
        if (userId < 0) {
             throw new NotFoundException("User Id must be positive.");
        }
        Optional<User> optional = repository.findById(userId);

        return optional.orElseThrow(() -> new DataNotFoundException(String.format("User id %d doesn't exist", userId)));
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
