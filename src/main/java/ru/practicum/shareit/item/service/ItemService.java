package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto editItem(long itemId, ItemDto itemDto);

    List<ItemDto> getItemsById(long userId);

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> searchItems(String searchText);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);


}
