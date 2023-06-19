package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
            .id(item.getId())
            .owner(item.getOwner().getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.isAvailable())
            .build();
    }
    public ItemDto toInfoItemDto(Item item) {
        return ItemDto.builder()
            .id(item.getId())
            .owner(item.getOwner().getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.isAvailable())
            .comments(new ArrayList<>())
            .build();
    }


    public Item toItem(ItemDto itemDto) {
        return Item.builder()
            .name(itemDto.getName())
            .description(itemDto.getDescription())
            .available(itemDto.getAvailable())
            .build();
    }
    public List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream()
            .map(this::toItemDto)
            .collect(Collectors.toList());
    }
}
