package ru.practicum.shareit.item.service;


import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final List<Item> items;
    private long count = 1;
    private final UserService userService;

    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
        this.items = new ArrayList<>();
    }

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        UserDto owner = userService.getUserById(userId);
        if (owner == null) {
            throw new DataNotFoundException("User not found");
        }
        System.out.println(itemDto.getClass().toString() + itemDto);
        itemDto.setOwnerId(UserMapper.INSTANCE.toUser(owner).getId());
        itemDto.setId(generateItemId());
        Item newItem = ItemMapper.INSTANCE.toItem(itemDto);
        items.add(newItem);
        return ItemMapper.INSTANCE.toItemDto(newItem);
    }

    @Override
    public ItemDto editItem(long itemId, ItemDto itemDto) {
        Item existingItem = getItemFromList(itemId);
        if (existingItem == null) {
            throw new DataNotFoundException("Item not found");
        }

        UserDto owner = userService.getUserById(existingItem.getOwnerId());
        if (owner == null) {
            throw new DataNotFoundException("User not found");
        }

        if (owner.getId() != itemDto.getOwnerId()) {
            throw new DataNotFoundException("User is not the owner of the item");
        }

        if (itemDto.getName() != null) existingItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) existingItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) existingItem.setAvailable(itemDto.getAvailable().booleanValue());

        return ItemMapper.INSTANCE.toItemDto(existingItem);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = getItemFromList(itemId);
        if (item == null) {
            throw new DataNotFoundException("Item not found");
        }

        return ItemMapper.INSTANCE.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(long ownerId) {
        List<ItemDto> ownerItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwnerId() == ownerId) {
                ownerItems.add(ItemMapper.INSTANCE.toItemDto(item));
            }
        }

        return ownerItems;
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        List<ItemDto> foundItems = new ArrayList<>();
        for (Item item : items) {
            if (item.isAvailable() &&
                    (item.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(searchText.toLowerCase()))) {
                foundItems.add(ItemMapper.INSTANCE.toItemDto(item));
            }
        }
        return foundItems;
    }

    private Item getItemFromList(long itemId) {
        for (Item item : items) {
            if (item.getId() == itemId) {
                return item;
            }
        }
        return null;
    }

    private long generateItemId() {

        return count++;
    }
}
