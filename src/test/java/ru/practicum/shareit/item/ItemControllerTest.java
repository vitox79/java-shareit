package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService service;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;

    private CommentDto commentDto;

    @BeforeEach
    void init() {
        itemDto = ItemDto.builder()
            .id(1L)
            .name("user")
            .available(true)
            .description("what")
            .owner(1L)
            .build();

        commentDto = CommentDto.builder().id(1L).text("text").build();
    }

    @Test
    void createItem() throws Exception {
        when(service.addItem(anyLong(), any()))
            .thenReturn(itemDto);

        mvc.perform(post("/items")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.writeValueAsString(itemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(itemDto.getName())))
            .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void createNewComment() throws Exception {
        when(service.addComment(anyLong(), anyLong(), any()))
            .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.writeValueAsString(commentDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
            .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }

    @Test
    void updateItem() throws Exception {
        itemDto.setName("this");
        itemDto.setOwner(1L);
        when(service.editItem(anyLong(), any()))
            .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                .header("X-Sharer-User-Id", 1)
                .content(mapper.writeValueAsString(itemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(itemDto.getName())))
            .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void getItem() throws Exception {
        when(service.getItemById(anyLong(), anyLong()))
            .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is(itemDto.getName())))
            .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void getItemsWithoutSize() throws Exception {
        when(service.getItemsById(anyLong(), anyInt(), anyInt()))
            .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void getItemsWithSize() throws Exception {
        when(service.getItemsById(anyLong(), anyInt(), anyInt()))
            .thenReturn(List.of(itemDto));

        mvc.perform(get("/items?from=0&size=5")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void searchItemsWithoutSize() throws Exception {
        when(service.searchItems(anyString(), anyInt(), anyInt()))
            .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=description")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void searchItemsWithSize() throws Exception {
        when(service.searchItems(anyString(), anyInt(), anyInt()))
            .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=description&from=0&size=2")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void searchItemsWithSizeZero() throws Exception {
        mvc.perform(get("/items/search?text=description&from=0&size=0")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void searchItemsWithSizeNegative() throws Exception {
        mvc.perform(get("/items/search?text=description&from=0&size=-1")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void searchItemsWithWrongFrom() throws Exception {
        mvc.perform(get("/items/search?text=description&from=-1&size=1")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getItemsWithSizeZero() throws Exception {
        mvc.perform(get("/items?from=0&size=0")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getItemsWithWrongSize() throws Exception {
        mvc.perform(get("/items?from=1&size=-2")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getItemsWithFromNegative() throws Exception {
        mvc.perform(get("/items?from=-1&size=1")
                .header("X-Sharer-User-Id", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

}