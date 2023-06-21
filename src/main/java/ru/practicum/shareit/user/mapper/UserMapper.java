package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }

    public static User toUser(UserDto user) {
        return User.builder()
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }

    public static List<UserDto> toItemDtoList(List<User> users) {
        return users.stream()
            .map(UserMapper::toUserDto)
            .collect(Collectors.toList());
    }

}
