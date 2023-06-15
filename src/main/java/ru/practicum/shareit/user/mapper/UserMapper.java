package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }

    public User toUser(UserDto user) {
        return User.builder()
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }
    public List<UserDto> toItemDtoList(List<User> users) {
        return users.stream()
            .map(this::toUserDto)
            .collect(Collectors.toList());
    }

}
