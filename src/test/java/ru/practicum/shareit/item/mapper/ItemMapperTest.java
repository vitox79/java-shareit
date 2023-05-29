package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {

    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Test
    public void testToItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Отвертка");
        item.setDescription("Аккумуляторная отвертка");
        item.setAvailable(true);
        item.setOwnerId(1L);

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.isAvailable(), itemDto.getAvailable().booleanValue());
        assertEquals(item.getOwnerId(), itemDto.getOwnerId());
    }
}
