package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SimpleRequestDto;

import java.util.ArrayList;

@Component
public class ItemRequestMapper {
    public static ItemRequest toItemRequest(SimpleRequestDto itemRequestDto) {
        return ItemRequest.builder()
            .description(itemRequestDto.getDescription())
            .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
            .id(itemRequest.getId())
            .description(itemRequest.getDescription())
            .created(itemRequest.getCreated())
            .items(new ArrayList<>())
            .build();
    }
}
