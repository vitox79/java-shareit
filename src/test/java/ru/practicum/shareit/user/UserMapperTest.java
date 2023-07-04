package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    @Test
    void toUserDto() {
        User user = User.builder().id(1L).name("name").email("email").build();
        UserDto userDto = UserMapper.toUserDto(user);

        assertEquals(user.getId(), userDto.getId(), "wrong id  in dto");
        assertEquals(user.getName(), userDto.getName(), "wrong name in dto");
        assertEquals(user.getEmail(), userDto.getEmail(), "wrong email in dto");
    }

    @Test
    void toUser() {
        UserDto userDto = UserDto.builder().name("name").email("email").build();
        User user = UserMapper.toUser(userDto);

        assertNull(user.getId(), "wrong id");
        assertEquals(userDto.getName(), user.getName(), "wong name");
        assertEquals(userDto.getEmail(), user.getEmail(), "wrong email");
    }
}