package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private UserMapper userMapper = UserMapper.INSTANCE;
    @Test
    public void testToUserDto() {
        // Создаем объект User
        User user = new User();
        user.setId(1L);
        user.setName("john_doe");
        user.setEmail("john@example.com");


        UserDto userDto = userMapper.toUserDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

}