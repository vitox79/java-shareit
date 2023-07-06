package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.UnknownArgumentException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class ErrorHandlerControllerTest {
    @MockBean
    private UserService service;

    @Autowired
    private MockMvc mvc;

    @Test
    void getNotFoundException() throws Exception {
        when(service.getUserById(anyLong())).thenThrow(new DataNotFoundException(""));

        mvc.perform(get("/users/1")).andExpect(status().isNotFound());
    }

    @Test
    void getIncorrectCountException() throws Exception {
        when(service.getUserById(anyLong())).thenThrow(new DataNotFoundException(""));

        mvc.perform(get("/users/1")).andExpect(status().isNotFound());
    }

    @Test
    void getRuntimeException() throws Exception {
        when(service.getUserById(anyLong())).thenThrow(new UnknownArgumentException(""));

        mvc.perform(get("/users/1")).andExpect(status().isBadRequest());
    }

    @Test
    void getUnknownStateException() throws Exception {
        when(service.getUserById(anyLong())).thenThrow(new NotFoundException("Не найдено"));

        mvc.perform(get("/users/1")).andExpect(status().isBadRequest());
    }
}