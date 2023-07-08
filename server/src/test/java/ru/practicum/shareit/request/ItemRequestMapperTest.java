package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SimpleRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemRequestMapperTest {

    @Test
    void toItemRequest() {
        SimpleRequestDto requestDto = SimpleRequestDto.builder()
            .description("description")
            .build();
        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto);

        assertEquals(requestDto.getDescription(), request.getDescription(), "wrong description в model");
    }

    @Test
    void toItemRequestDto() {
        ItemRequest request = ItemRequest.builder()
            .id(1L)
            .created(LocalDateTime.now())
            .description("description").build();
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);

        assertEquals(request.getId(), requestDto.getId(), "wrong id");
        assertEquals(requestDto.getDescription(), request.getDescription(), "wrong description в model");
        assertEquals(request.getCreated(), requestDto.getCreated(), "wrong created");
        assertNull(request.getOwner(), "wrong owner");
    }

}