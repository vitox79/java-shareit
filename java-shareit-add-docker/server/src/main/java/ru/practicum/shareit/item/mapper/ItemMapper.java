package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
            .id(item.getId())
            .owner(item.getOwner().getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.isAvailable())
            .build();
    }

    public static ItemDto toInfoItemDto(Item item) {
        return ItemDto.builder()
            .id(item.getId())
            .owner(item.getOwner().getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.isAvailable())
            .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
            .comments(new ArrayList<>())
            .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
            .name(itemDto.getName())
            .description(itemDto.getDescription())
            .available(itemDto.getAvailable())
            .build();
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream()
            .map(ItemMapper::toItemDto)
            .collect(Collectors.toList());
    }
}
