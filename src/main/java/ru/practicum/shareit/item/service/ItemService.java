package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto editItem(long itemId, ItemDto itemDto);

    List<ItemDto> getItemsById(long userId, int from, int size);

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> searchItems(String searchText, int from, int size);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);

     ItemRequest getRequestById(long requestId);

}
