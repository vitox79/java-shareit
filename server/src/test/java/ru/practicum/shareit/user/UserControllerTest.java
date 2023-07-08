package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    private UserService service;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;

    @BeforeEach
    void init() {
        userDto = UserDto.builder().id(1L).name("name").email("mail@mail.ru").build();
    }

    @Test
    void createUser() throws Exception {
        User user = UserMapper.toUser(userDto);
        when(service.createUser(any()))
            .thenReturn(user);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(user.getName())))
            .andExpect(jsonPath("$.email", is(user.getEmail())));
    }


    @Test
    void updateUser() throws Exception {
        userDto.setName("Viktor");
        User user = UserMapper.toUser(userDto);
        user.setId(1L);
        when(service.updateUser(anyLong(), any()))
            .thenReturn(user);

        mvc.perform(patch("/users/1")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is("Viktor")))
            .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getUser() throws Exception {
        User user = UserMapper.toUser(userDto);
        user.setId(1L);
        when(service.getUserById(anyLong()))
            .thenReturn(user);

        mvc.perform(get("/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(userDto.getName())))
            .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getUsers() throws Exception {
        UserDto userDto1 = UserDto.builder().id(2L).name("Boss").email("vvv@mail.ru").build();
        User user = UserMapper.toUser(userDto);
        user.setId(1L);
        User user2 = UserMapper.toUser(userDto1);
        user2.setId(2L);
        when(service.getAllUsers()).thenReturn(Arrays.asList(user, user2));
        mvc.perform(
                get("/users"))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(Arrays.asList(userDto, userDto1))));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/1"))
            .andExpect(status().isOk());
    }
}