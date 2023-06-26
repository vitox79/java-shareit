package ru.practicum.shareit.item.comment.repository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Transactional
    Optional<List<Comment>> findAllByItemId(Long itemId);

    Optional<Object> findByItemIn(List<Item> items, Sort created);

    List<Comment> findByItemInOrderByCreatedDesc(List<Item> items, Sort sort);
}

