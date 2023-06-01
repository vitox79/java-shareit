package ru.practicum.shareit.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class StorageUser {
    private final Map<Long, User> userMap;
    private long userIdGenerator;

    public StorageUser() {
        this.userMap = new HashMap<>();
        this.userIdGenerator = 1L;
    }

    public UserDto createUser(User user) {
        if (isEmailDuplicate(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        long userId = userIdGenerator++;
        user.setId(userId);
        userMap.put(userId, user);
        return UserMapper.INSTANCE.toUserDto(user);
    }

    public UserDto getUserById(long userId) {
        User user = userMap.get(userId);
        if (user == null) {
            throw new DataNotFoundException("User not found");
        }
        return UserMapper.INSTANCE.toUserDto(user);
    }

    public UserDto updateUser(long userId, User updatedUser) {
        User user = userMap.get(userId);
        if (user == null) {
            throw new DataNotFoundException("User not found");
        }

        if (isEmailDuplicate(updatedUser.getEmail(), userId)) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }

        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        return UserMapper.INSTANCE.toUserDto(user);
    }

    public void deleteUser(long userId) {
        User user = userMap.remove(userId);
        if (user == null) {
            throw new DataNotFoundException("User not found");
        }
    }

    private boolean isEmailDuplicate(String email) {
        return userMap.values().stream()
            .anyMatch(user -> user.getEmail().equals(email));
    }

    private boolean isEmailDuplicate(String email, long userId) {
        return userMap.values().stream()
            .filter(u -> u.getId() != userId)
            .anyMatch(u -> u.getEmail().equals(email));
    }

    public List<UserDto> getAllUsers() {
        return userMap.values().stream()
            .map(UserMapper.INSTANCE::toUserDto)
            .collect(Collectors.toList());
    }
}
