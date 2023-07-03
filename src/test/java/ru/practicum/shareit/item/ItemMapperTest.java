package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {
    private final ItemMapper mapper = new ItemMapper();

    @Test
    void toItemDtoWithoutRequestId() {
        Item item = Item.builder()
                .id(1L)
                .owner(User.builder().id(1L).email("mail").name("name").build())
                .name("name")
                .description("description")
                .available(true)
                .build();
        ItemDto itemDto = mapper.toItemDto(item);

        assertEquals(item.getDescription(), itemDto.getDescription(), "Wrong description");
        assertEquals(item.getName(), itemDto.getName(), "Wrong name");
        assertEquals(item.isAvailable(), itemDto.getAvailable(), "Wrong available");
        assertNotNull(itemDto.getComments(), "Wrong comments");
        assertNull(itemDto.getRequestId(), "Wrong requestId");
    }

    @Test
    void toItemDtoWithRequestId() {
        Item item = Item.builder()
                .id(1L)
                .owner(User.builder().id(1L).email("mail").name("name").build())
                .name("name")
                .description("description")
                .available(true)
                .request(ItemRequest.builder().id(1L).description("description").build())
                .build();
        ItemDto itemDto = mapper.toItemDto(item);

        assertEquals(item.getDescription(), itemDto.getDescription(), "wrong description");
        assertEquals(item.getName(), itemDto.getName(), "wrong name");
        assertEquals(item.isAvailable(), itemDto.getAvailable(), "wrong available");
        assertEquals(item.getRequest().getId(), itemDto.getRequestId(), "wrong requestId");
        assertNotNull(itemDto.getComments(), "wrong comments");
    }

    @Test
    void toItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(false)
                .build();
        Item item = mapper.toItem(itemDto);

        assertEquals(itemDto.getDescription(), item.getDescription(), "description не присваивается в model");
        assertEquals(itemDto.getName(), item.getName(), "name не присваивается в model");
        assertEquals(itemDto.getAvailable(), item.isAvailable(), "available не присваивается в model");
        assertNull(item.getOwner(), "owner в model не null");
        assertNull(item.getRequest(), "request в model не null");
    }
}