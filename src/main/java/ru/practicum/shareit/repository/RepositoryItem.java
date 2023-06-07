package ru.practicum.shareit.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RepositoryItem {
    private final Map<Long, Item> items;
    private final Map<Long, List<Item>> userItemIndex;
    private long count = 1;

    public RepositoryItem() {
        this.items = new LinkedHashMap<>();
        this.userItemIndex = new LinkedHashMap<>();
    }

    public void addItem(Item item) {
        item.setId(generateItemId());
        items.put(item.getId(), item);
        userItemIndex.computeIfAbsent(item.getOwnerId(), key -> new ArrayList<>()).add(item);
    }

    public void updateItem(Item item) {
        if (items.containsKey(item.getId())) {
            items.put(item.getId(), item);
        }
    }

    public Item getItemById(long itemId) {
        return items.get(itemId);
    }

    public List<Item> getItemsByOwnerId(long ownerId) {
        return userItemIndex.getOrDefault(ownerId, Collections.emptyList());
    }

    public List<Item> searchItems(String searchText) {
        return items.values().stream()
            .filter(item -> item.isAvailable() &&
                (item.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(searchText.toLowerCase())))
            .collect(Collectors.toList());
    }

    private long generateItemId() {
        return count++;
    }
}
