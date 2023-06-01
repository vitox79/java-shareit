package ru.practicum.shareit.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StorageItem {
    private final Map<Long, Item> items;
    private long count = 1;

    public StorageItem() {
        this.items = new HashMap<>();
    }

    public void addItem(Item item) {
        item.setId(generateItemId());
        items.put(item.getId(), item);
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
        return items.values().stream()
            .filter(item -> item.getOwnerId() == ownerId)
            .collect(Collectors.toList());
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
