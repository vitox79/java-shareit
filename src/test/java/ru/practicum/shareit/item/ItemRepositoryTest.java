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
        Item item = Item.builder().id(7L).name("item").description("that").available(true).build();
        Item item2 = Item.builder().id(1L).name("item1").description("what").available(true).build();

        repository.save(item);
        repository.save(item2);

        List<Item> items = repository.search("what", PageRequest.of(0, 3))
            .stream().collect(Collectors.toList());

        assertEquals(1, items.size(), "Wrong  search");
        assertEquals(item2.getId(), items.get(0).getId(), "Wrong item ");
    }
}