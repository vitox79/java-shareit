package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.Optional;


public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerIdOrderByIdAsc(Long userId);

    @Query("select i from Item i" +
        " where (upper(i.name) like upper(concat('%', ?1, '%')) " +
        " or upper(i.description) like upper(concat('%', ?1, '%'))) " +
        " and (i.available = true)")
    List<Item> search(String text);

    List<Item> findByRequestInOrderByIdAsc(List<ItemRequest> requests);
}
