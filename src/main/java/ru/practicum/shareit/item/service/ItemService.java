package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto editItem(long itemId, ItemDto itemDto);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItemsByOwnerId(long ownerId);

    List<ItemDto> searchItems(String searchText);
}
