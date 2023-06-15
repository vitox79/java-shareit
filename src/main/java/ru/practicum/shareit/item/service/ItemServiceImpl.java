package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.RepositoryUser;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final RepositoryUser userRepository;

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElse(null);
        if (owner == null) {
            throw new DataNotFoundException("User not found");
        }
        itemDto.setOwner(owner.getId());
        Item newItem = itemMapper.toItem(itemDto);
        newItem.setOwner(owner);
        itemRepository.save(newItem);
        return itemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto editItem(long itemId, ItemDto itemDto) {
        Optional <Item> existingItem = itemRepository.findById(itemId);
        if (!existingItem.isPresent()) {
            throw new DataNotFoundException("Item not found");
        }

        if (existingItem.get().getOwner().getId()!= (itemDto.getOwner())) {
            throw new DataNotFoundException("User is not the owner of the item");
        }

        if (itemDto.getName() != null) {
            existingItem.get().setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.get().setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.get().setAvailable(itemDto.getAvailable());
        }

        itemRepository.save(existingItem.get());

        return itemMapper.toItemDto(existingItem.get());
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            throw new DataNotFoundException("Item not found");
        }

        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(long ownerId) {
        return itemMapper.toItemDtoList(itemRepository.findByOwnerIdOrderByIdAsc(ownerId));
    }

    @Override
    public List<ItemDto> searchItems(String searchText) {
        return
            itemMapper.toItemDtoList(itemRepository.search(searchText));
    }
}