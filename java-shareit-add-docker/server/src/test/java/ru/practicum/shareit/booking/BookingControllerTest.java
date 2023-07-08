package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingService service;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;

    @BeforeEach
    void init() {
        bookingDto = BookingDto.builder()
            .id(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now())
            .build();
    }

    @Test
    void create() throws Exception {
        when(service.create(anyLong(), any()))
            .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.registerModule(new JavaTimeModule())
                    .writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    void update() throws Exception {
        bookingDto.setItemId(1L);

        when(service.update(anyLong(), anyLong(), anyBoolean()))
            .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1?approved=true")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.registerModule(new JavaTimeModule())
                    .writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
            .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class));
    }

    @Test
    void getBooking() throws Exception {
        when(service.getById(anyLong(), anyLong()))
            .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    void getAllByUser() throws Exception {
        bookingDto.setStart(null);
        bookingDto.setEnd(null);
        when(service.getAllByUser(anyLong(), anyString(), anyInt(), anyInt()))
            .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.registerModule(new JavaTimeModule())
                    .writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getAllByOwner() throws Exception {
        bookingDto.setStart(null);
        bookingDto.setEnd(null);
        when(service.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt()))
            .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.registerModule(new JavaTimeModule())
                    .writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    void getAllByOwnerSizeZero() throws Exception {
        mvc.perform(get("/bookings/owner?from=0&size=0")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.registerModule(new JavaTimeModule())
                    .writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBookingByOwnerSizeNegative() throws Exception {
        mvc.perform(get("/bookings/owner?from=0&size=-1")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.registerModule(new JavaTimeModule())
                    .writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBookingByOwnerFromNegative() throws Exception {
        mvc.perform(get("/bookings/owner?from=-1&size=1")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.registerModule(new JavaTimeModule())
                    .writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAllByUserSizeWrong() throws Exception {
        mvc.perform(get("/bookings?from=0&size=-1")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.registerModule(new JavaTimeModule())
                    .writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAllByUserNone() throws Exception {
        mvc.perform(get("/bookings?from=0&size=0")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.registerModule(new JavaTimeModule())
                    .writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAllByUserFromWrong() throws Exception {
        mvc.perform(get("/bookings?from=-1&size=1")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.registerModule(new JavaTimeModule())
                    .writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

}