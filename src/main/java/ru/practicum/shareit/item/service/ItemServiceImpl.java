package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.storage.StorageItem;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final StorageItem storageItem;
    private final UserService userService;

    public ItemServiceImpl(StorageItem storageItem, UserService userService) {
        this.storageItem = storageItem;
        this.userService = userService;
    }

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        UserDto owner = userService.getUserById(userId);
        if (owner == null) {
            throw new DataNotFoundException("User not found");
        }
        itemDto.setOwnerId(UserMapper.INSTANCE.toUser(owner).getId());
        Item newItem = ItemMapper.INSTANCE.toItem(itemDto);
        storageItem.addItem(newItem);
        return ItemMapper.INSTANCE.toItemDto(newItem);
    }

    @Override
    public ItemDto editItem(long itemId, ItemDto itemDto) {
        Item existingItem = storageItem.getItemById(itemId);
        if (existingItem == null) {
            throw new DataNotFoundException("Item not found");
        }

        if (existingItem.getOwnerId() != itemDto.getOwnerId()) {
            throw new DataNotFoundException("User is not the owner of the item");
        }

        if (itemDto.getName() != null) existingItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) existingItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) existingItem.setAvailable(itemDto.getAvailable().booleanValue());

        storageItem.updateItem(existingItem);

        return ItemMapper.INSTANCE.toItemDto(existingItem);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = storageItem.getItemById(itemId);
        if (item == null) {
            throw new DataNotFoundException("Item not found");
        }

        return ItemMapper.INSTANCE.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(long ownerId) {
        List<Item> ownerItems = storageItem.getItemsByOwnerId(ownerId);
        return ownerItems.stream()
            .map(ItemMapper.INSTANCE::toItemDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        List<Item> foundItems = storageItem.searchItems(searchText);
        return foundItems.stream()
            .map(ItemMapper.INSTANCE::toItemDto)
            .collect(Collectors.toList());
    }
}
