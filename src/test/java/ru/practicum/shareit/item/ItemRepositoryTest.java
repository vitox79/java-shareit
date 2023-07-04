package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository repository;

    @Test
    void search() {
        Item item = Item.builder().id(1L).name("item").description("that").available(true).build();
        Item item2 = Item.builder().id(2L).name("item1").description("qqq").available(true).build();

        repository.save(item);
        Item item1 = repository.save(item2);

        List<Item> items = repository.search("qqq", PageRequest.of(0, 3))
            .getContent()
            .stream()
            .collect(Collectors.toList());

        assertEquals(1, items.size(), "Wrong search");
        assertEquals(item1.getId(), items.get(0).getId(), "Wrong item");
    }
}